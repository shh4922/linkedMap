package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.config.JWTProvider;
import com.hyeonho.linkedmap.config.RefreshToken;
import com.hyeonho.linkedmap.data.dto.LoginResponseDTO;
import com.hyeonho.linkedmap.data.request.LoginRequestDTO;
import com.hyeonho.linkedmap.data.request.RegisterRequest;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.enumlist.Role;
import com.hyeonho.linkedmap.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTProvider jwtProvider;

    public Member register(RegisterRequest member) {
        String password = bCryptPasswordEncoder.encode(member.getPassword());
        Member member1 = new Member(member.getEmail(), password, member.getUsername(), Role.ROLE_USER);
        return memberRepository.save(member1);
    }

    public Member findByEmail(String email) {
        return memberRepository.findById(email).orElseThrow();
    }

    public LoginResponseDTO login(final LoginRequestDTO loginRequestDTO) {
        // 사용자 정보 조회
        Member userInfo = findByEmail(loginRequestDTO.getEmail());

        // password 일치 여부 체크
        if(!bCryptPasswordEncoder.matches(loginRequestDTO.getPassword(), userInfo.getPassword()))
            throw new RuntimeException("비밀번호가 일치하지않음");

        // jwt 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(userInfo.getEmail());


        // 기존에 가지고 있는 사용자의 refresh token 제거
        RefreshToken.removeUserRefreshToken(userInfo.getEmail());

        // refresh token 생성 후 저장
        String refreshToken = jwtProvider.generateRefreshToken(userInfo.getEmail());
        RefreshToken.putRefreshToken(refreshToken, userInfo.getEmail());

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
