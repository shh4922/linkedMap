package com.hyeonho.linkedmap.config;

import com.hyeonho.linkedmap.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;
    private final MemberService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token = request.getHeader("Authorization");

        String username = null;

        if(token != null && !token.isEmpty()) {
            String jwtToken = token.substring(7);
            username = jwtProvider.getUsernameFromToken(jwtToken);
        }

        /**
         * getAuthentication() == null이 의미하는 것
         * 클라이언트가 API 요청을 보냄 (HttpServletRequest)
         * 요청 헤더에서 JWT 토큰을 가져옴 (Authorization 헤더)
         * jwtProvider.getUsernameFromToken(jwtToken)을 통해 유저명을 가져옴
         * SecurityContextHolder.getContext().getAuthentication()을 체크
         * null이면: 아직 인증되지 않은 상태 → 인증을 진행
         * null이 아니면: 이미 인증된 상태 → 인증을 다시 할 필요 없음
         * 📌 즉, 이 조건이 없으면 매번 불필요하게 인증이 실행될 수 있음
         *
         */
        if(username != null && !username.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(getUserAuth(username));
        }

        filterChain.doFilter(request, response);
    }

    /**
     * token의 사용자 idx를 이용하여 사용자 정보 조회하고, UsernamePasswordAuthenticationToken 생성
     * 비밀번호는 딱히 저장안해도 된다고함, user정보랑, 권한만 저장하면 된다고 함
     * @param email 사용자 idx
     * @return 사용자 UsernamePasswordAuthenticationToken
     */
    private UsernamePasswordAuthenticationToken getUserAuth(String email) {
        var userInfo = memberService.findByEmail(email);

        return new UsernamePasswordAuthenticationToken(userInfo.getEmail(),
                userInfo.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(userInfo.getRole().value()))
        );
    }
}
