package com.hyeonho.linkedmap.controller;

import com.hyeonho.linkedmap.config.JWTProvider;
import com.hyeonho.linkedmap.config.JwtAuthFilter;
import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.data.dto.LoginResponseDTO;
import com.hyeonho.linkedmap.data.dto.MemberDeleteDto;
import com.hyeonho.linkedmap.data.dto.MemberInfoDTO;
import com.hyeonho.linkedmap.data.dto.MemberUpdateDto;
import com.hyeonho.linkedmap.data.request.LoginRequestDTO;
import com.hyeonho.linkedmap.data.request.MemberUpdateRequest;
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

import java.util.HashMap;
import java.util.Map;

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

    /**
     * 로그인
     * 이메일이랑 비밀번호 받음.
     * @param request
     * @return 응답은 access, refresh 토큰 던짐
     */
    @PostMapping("/login")
    public ResponseEntity<DefaultResponse<LoginResponseDTO>> login(@RequestBody LoginRequestDTO request) {
        if(request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(DefaultResponse.error(400, "아이디 비밀번호를 모두 입력하세요."));
        }
        LoginResponseDTO responseDTO = memberService.login(request);
        return ResponseEntity.ok(DefaultResponse.success(responseDTO));
    }

    /**
     * 내 정보 조회
     * 내 정보 찾은후, 이메일, 이름, 역할만 리턴함
     * @param headers
     * @return 이메일, 이름, 역할 리턴
     */
    @GetMapping("/user/my")
    public ResponseEntity<DefaultResponse<MemberInfoDTO>> getMyInfo(@RequestHeader HttpHeaders headers) {
        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);

        if(authorization == null) return ResponseEntity.badRequest().body(DefaultResponse.error(400, "내정보 받아오는데 실패함"));


        String email = jwtProvider.getUsernameFromToken(authorization);
        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 이메일의 사용자를 찾을 수 없음"));

        MemberInfoDTO res = new MemberInfoDTO();
        res.setEmail(member.getEmail());
        res.setUsername(member.getUsername());
        res.setRole(member.getRole());
        return ResponseEntity.ok(DefaultResponse.success(res));
    }

    /**
     * 유저 정보 조회
     * 유저 email 로 정보 조회함
     * @param headers
     * @param email
     * @return 이름, 이메일, 역할 리턴함
     */
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

    /**
     * 유저 정보 수정
     * 그런데 수정할게 이름말고는 없음 ㅅㅂ;;
     * @param headers
     * @param request
     * @return
     */
    @PatchMapping("/user/info")
    public ResponseEntity<DefaultResponse<MemberUpdateDto>> updateMemberInfo(@RequestHeader HttpHeaders headers, MemberUpdateRequest request) {
        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if(authorization == null) {
            return ResponseEntity.badRequest()
                    .body(DefaultResponse.error(402, "토큰없음"));
        }

        String email = jwtProvider.getUsernameFromToken(authorization);
        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 이메일의 사용자를 찾을 수 없음"));

        Member updatedMember = memberService.updateMemberInfo(member,request);
        MemberUpdateDto memberUpdateDto = new MemberUpdateDto();

        memberUpdateDto.setEmail(updatedMember.getEmail());
        memberUpdateDto.setUsername(updatedMember.getUsername());
        memberUpdateDto.setCreatedAt(updatedMember.getCreatedAt());
        memberUpdateDto.setUpdateAt(updatedMember.getUpdatedAt());

        return ResponseEntity.ok(DefaultResponse.success(memberUpdateDto));
    }

    /**
     * 회원탈퇴
     * 회원탈퇴시, 해당 유저가 만든 카테고리 삭제 로직을 태워야함.
     * 내가 만든 카테고리는 삭제처리 해야함
     * 내가 속한 카테고리는 나가기 처리 해야함.
     */
    @DeleteMapping("/user/delete")
    public ResponseEntity<DefaultResponse<MemberDeleteDto>> deleteMember(@RequestHeader HttpHeaders headers) {
        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if(authorization == null) {
            return ResponseEntity.badRequest()
                    .body(DefaultResponse.error(402, "토큰없음"));
        }

        String email = jwtProvider.getUsernameFromToken(authorization);
        Member member = memberService.deleteMember(email);
        if(member.getDeletedAt() != null) {
            MemberDeleteDto deleteDto = MemberDeleteDto.builder()
                    .username(member.getUsername())
                    .email(member.getEmail())
                    .deletedAt(member.getDeletedAt())
                    .build();
            return ResponseEntity.ok(DefaultResponse.success(deleteDto));
        }

        return ResponseEntity.badRequest()
                .body(DefaultResponse.error(400, "삭제실패"));
    }
}
