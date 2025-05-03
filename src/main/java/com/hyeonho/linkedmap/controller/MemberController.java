package com.hyeonho.linkedmap.controller;

import com.hyeonho.linkedmap.config.JWTProvider;
import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.data.dto.member.*;
import com.hyeonho.linkedmap.data.request.LoginRequestDTO;
import com.hyeonho.linkedmap.data.request.MemberUpdateRequest;
import com.hyeonho.linkedmap.data.request.RegisterRequest;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<DefaultResponse<RegisterDTO>> register(@RequestBody RegisterRequest request) {
        if(request.getEmail() == null || request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(DefaultResponse.error(400, "정보를 모두 입력하세요"));
        }

        return ResponseEntity.ok(DefaultResponse.success(memberService.register(request)));
    }

    /**
     * 로그인
     * 이메일이랑 비밀번호 받음.
     * 탈퇴한 회원일떄의 처리
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
     * @return 이메일, 이름, 역할 리턴
     */
    @GetMapping("/user/my")
    public ResponseEntity<DefaultResponse<MemberInfoDTO>> getMyInfo(@AuthenticationPrincipal Long id) {
        return ResponseEntity.ok(DefaultResponse.success(memberService.getMyInfoById(id)));
    }

    /**
     * 유저 정보 조회
     * 유저 email 로 정보 조회함
     * @param email
     * @return 이름, 이메일, 역할 리턴함
     */
    @GetMapping("/users/info/{email}")
    public ResponseEntity<DefaultResponse<MemberInfoDTO>> getMemberInfoByEmail(@PathVariable(value = "email") String email) {
        return ResponseEntity.ok(DefaultResponse.success(memberService.getUserInfoByEmail(email)));
    }

    /**
     * 유저 정보 수정
     * 그런데 수정할게 이름말고는 없음 ㅅㅂ;;
     * @param request
     * @return
     */
    @PatchMapping("/user/info")
    public ResponseEntity<DefaultResponse<MemberUpdateDto>> updateMemberInfo(@AuthenticationPrincipal Long id, MemberUpdateRequest request) {
        return ResponseEntity.ok(DefaultResponse.success(memberService.updateMemberInfo(id, request)));
    }

    /**
     * 회원탈퇴
     * 회원탈퇴시, 해당 유저가 만든 카테고리 삭제 로직을 태워야함.
     * 내가 만든 카테고리는 삭제처리 해야함
     * 내가 속한 카테고리는 나가기 처리 해야함.
     */
    @DeleteMapping("/user/delete")
    public ResponseEntity<DefaultResponse<String>> deleteMember(@AuthenticationPrincipal Long memberId) {

        Member member = memberService.deleteMember(memberId);

        if(member.getDeletedAt() != null) {
            return ResponseEntity.ok(DefaultResponse.success("0"));
        }

        return ResponseEntity.badRequest()
                .body(DefaultResponse.error(400, "삭제실패"));
    }
}
