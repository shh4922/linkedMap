package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.config.JWTProvider;
import com.hyeonho.linkedmap.config.RefreshToken;
import com.hyeonho.linkedmap.controller.MemberController;
import com.hyeonho.linkedmap.data.dto.LoginResponseDTO;
import com.hyeonho.linkedmap.data.request.LoginRequestDTO;
import com.hyeonho.linkedmap.data.request.RegisterRequest;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.enumlist.Role;
import com.hyeonho.linkedmap.error.DatabaseException;
import com.hyeonho.linkedmap.error.DuplicateMemberException;
import com.hyeonho.linkedmap.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTProvider jwtProvider;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Member register(RegisterRequest request) {
        Optional<Member> member = findByEmail(request.getEmail());
        if(member.isPresent()) { throw new DuplicateMemberException("중복된 계정임");}

        try {
            String password = bCryptPasswordEncoder.encode(request.getPassword());
            Member member1 = new Member(request.getEmail(), password, request.getUsername(), Role.ROLE_USER);
            return memberRepository.save(member1);
        } catch (DatabaseException e) {
            throw new DatabaseException("회원가입-디비오류");
        }
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findById(email); // orElseThrow() 제거
    }


    public LoginResponseDTO login(final LoginRequestDTO loginRequestDTO) {

        Member userInfo = findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("해당 계정 없음"));

        // password 일치 여부 체크
        if(!bCryptPasswordEncoder.matches(loginRequestDTO.getPassword(), userInfo.getPassword()))
            throw new RuntimeException("비밀번호 틀림");

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
