package com.hyeonho.linkedmap.controller;

import com.hyeonho.linkedmap.config.JWTProvider;
import com.hyeonho.linkedmap.config.JwtAuthFilter;
import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.data.dto.LoginResponseDTO;
import com.hyeonho.linkedmap.data.dto.MemberInfoDTO;
import com.hyeonho.linkedmap.data.request.LoginRequestDTO;
import com.hyeonho.linkedmap.data.request.RegisterRequest;
import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.error.DatabaseException;
import com.hyeonho.linkedmap.error.DuplicateMemberException;
import com.hyeonho.linkedmap.service.MemberService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JWTProvider jwtProvider;
    private final Logger log = LoggerFactory.getLogger(getClass());


    /** 회원가입 */
    @PostMapping("/register")
    public ResponseEntity<DefaultResponse<Member>> register(@RequestBody RegisterRequest request) {
        if(request.getEmail() == null || request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(DefaultResponse.error(400, "정보를 모두 입력하세요"));
        }

        Member member = memberService.register(request);
        return ResponseEntity.ok(DefaultResponse.success(member));
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO responseDTO = memberService.login(request);
        return responseDTO;
    }

    @GetMapping("/user/my")
//    @PreAuthorize("isAuthenticated()")  // 🔥 인증된 사용자만 접근 가능하도록 설정
    public ResponseEntity<DefaultResponse<MemberInfoDTO>> getMyInfo(@RequestHeader HttpHeaders headers) {

        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if(authorization != null) {
            String email = jwtProvider.getUsernameFromToken(authorization);
            Member member = memberService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("해당 이메일의 사용자를 찾을 수 없음"));

            MemberInfoDTO res = new MemberInfoDTO();
            res.setEmail(member.getEmail());
            res.setUsername(member.getUsername());
            res.setRole(member.getRole());
            return ResponseEntity.ok(DefaultResponse.success(res));
        }
        return ResponseEntity.badRequest()
                .body(DefaultResponse.error(400, "내정보 받아오는데 실패함"));
    }

    @GetMapping("/users/info")
    public ResponseEntity<DefaultResponse<MemberInfoDTO>> getMemberInfoByEmail(@RequestHeader HttpHeaders headers, @RequestParam String email) {

        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if(authorization == null) {
            return ResponseEntity.badRequest()
                    .body(DefaultResponse.error(402, "토큰없음"));
        }

        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 이메일의 사용자를 찾을 수 없음"));

        if(member == null){
            return ResponseEntity.badRequest()
                    .body(DefaultResponse.error(400, "해당유저 없음"));
        }

        MemberInfoDTO res = new MemberInfoDTO();
        res.setEmail(member.getEmail());
        res.setUsername(member.getUsername());
        res.setRole(member.getRole());
        return ResponseEntity.ok(DefaultResponse.success(res));
    }

}
