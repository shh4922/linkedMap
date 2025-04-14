package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.config.JWTProvider;
import com.hyeonho.linkedmap.config.RefreshToken;
import com.hyeonho.linkedmap.controller.MemberController;
import com.hyeonho.linkedmap.data.dto.LoginResponseDTO;
import com.hyeonho.linkedmap.data.request.LoginRequestDTO;
import com.hyeonho.linkedmap.data.request.MemberUpdateRequest;
import com.hyeonho.linkedmap.data.request.RegisterRequest;
import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.entity.CategoryUser;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.enumlist.CategoryUserRole;
import com.hyeonho.linkedmap.enumlist.InviteState;
import com.hyeonho.linkedmap.enumlist.Role;
import com.hyeonho.linkedmap.error.DatabaseException;
import com.hyeonho.linkedmap.error.DuplicateMemberException;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final CategoryService categoryService;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTProvider jwtProvider;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Member register(RegisterRequest request) {
        Optional<Member> member = findByEmail(request.getEmail());
        if(member.isPresent()) { throw new DuplicateMemberException("중복된 계정임");}

        try {
            String password = bCryptPasswordEncoder.encode(request.getPassword());
            Member member1 = Member.builder()
                    .email(request.getEmail())
                    .password(password)
                    .username(request.getUsername())
                    .role(Role.ROLE_USER)
                    .build();

            return memberRepository.save(member1);
        } catch (DatabaseException e) {
            throw new DatabaseException("회원가입-디비오류");
        }
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmailAndDeletedAtIsNull(email);
    }


    public LoginResponseDTO login(final LoginRequestDTO loginRequestDTO) {

        Member userInfo = findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() ->  new InvalidRequestException("해당 계정 없음."));

        // password 일치 여부 체크
        if(!bCryptPasswordEncoder.matches(loginRequestDTO.getPassword(), userInfo.getPassword())) {
            throw new InvalidRequestException("비밀번호 틀림");
        }

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

    @Transactional
    public Member updateMemberInfo(Member member, MemberUpdateRequest request) {
        member.update(request.getUsername());
        return member;
    }

    @Transactional
    public Member deleteMember(String email) {
        Member member = findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 계정 없음"));

        /** 맴버 delete 상태 변경*/
        member.delete();

        /** 내가 속한 카테고리 조회*/
        List<CategoryUser> categoryList = categoryService.getIncludeCategoryByEmail(member.getEmail(), InviteState.INVITE);

        categoryList.forEach(categoryUser -> {
            if(categoryUser.getCategoryUserRole() == CategoryUserRole.OWNER) { // 본인이 만든거면 카테고리 삭제
                categoryService.deleteCategory(member.getEmail(), categoryUser.getCategory().getId());
            } else { // 다른 카테고리에 속한거라면 나가기 처리
                categoryService.getOutCategory(member.getEmail(), categoryUser.getCategory().getId());
            }
        });

        return member;
    }
}
