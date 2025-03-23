package com.hyeonho.linkedmap.controller;

import com.hyeonho.linkedmap.config.JWTProvider;
import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.data.dto.MemberInfoDTO;
import com.hyeonho.linkedmap.data.request.CategoryJoin;
import com.hyeonho.linkedmap.data.request.InviteCreateReq;
import com.hyeonho.linkedmap.data.request.RegisterRequest;
import com.hyeonho.linkedmap.entity.*;
import com.hyeonho.linkedmap.enumlist.CategoryUserRole;
import com.hyeonho.linkedmap.enumlist.InviteState;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.repository.CategoryUserRepository;
import com.hyeonho.linkedmap.service.CategoryService;
import com.hyeonho.linkedmap.service.InviteService;
import com.hyeonho.linkedmap.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InviteController {

    private final JWTProvider jwtProvider;
    private final InviteService inviteService;
    private final CategoryUserRepository categoryUserRepository;
    private final CategoryService categoryService;
    private final MemberService memberService;

    @PostMapping("/invite/create")
    public ResponseEntity<DefaultResponse<Map<String, String>>> createInvite(@RequestHeader HttpHeaders headers, @RequestBody InviteCreateReq req) {

        if(req.getCategoryId() == null) {
            throw new InvalidRequestException("categoryId 빔");
        }

        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if(authorization != null) {
            String email = jwtProvider.getUsernameFromToken(authorization);
            Invite invite = inviteService.createInvite(email,req.getCategoryId());
            String url = String.format("https://www.linkedmap.com/invite/%s/%s", invite.getCategoryId(), invite.getInviteKey());

            Map<String, String> response = new HashMap<>();
            response.put("url", url);
            return ResponseEntity.ok(DefaultResponse.success(response));
        }

        return ResponseEntity.badRequest()
                .body(DefaultResponse.error(400, "초대링크 만들기 실패함"));
    }

    /**
     * 초대된 카테고리 가입
     * 해당 카테고리ID의 UUDID가 있는 invite가 있는지 체크
     * 있다면 expire체크
     * 조건이 만족하면 가입
     * @return
     */
    @PostMapping("/invite/join")
    public ResponseEntity<DefaultResponse<CategoryUser>> joinCategory(@RequestHeader HttpHeaders headers, @RequestBody CategoryJoin req) {
        if(req.getCategoryId() == null || req.getInviteKey() == null) {
            throw new InvalidRequestException("데이터 비었음");
        }

        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if(authorization != null) {
            String email = jwtProvider.getUsernameFromToken(authorization);

            Invite invite = inviteService.findInviteByUUID(req.getInviteKey());
            if(invite == null) {
                return ResponseEntity.ok(DefaultResponse.error(410,"없는 초대링크임"));
            }

            // 기간 만료시, 해당 초대 닫고, 기한만료 전달
            if(LocalDateTime.now().isAfter(invite.getExpireAt())) {
                inviteService.updateInviteStateByUUID(invite.getInviteKey(), InviteState.TIMEOUT);
                return ResponseEntity.ok(DefaultResponse.error(400,"기한 만료"));
            }

            int result = inviteService.updateInviteMemberByUUID(invite.getInviteKey(), email, InviteState.INVITE);
            if(result != 1) {
                return ResponseEntity.ok(DefaultResponse.error(400,"초대상태 업데이트 실패"));
            }

            Category category = categoryService.findCategoryById(req.getCategoryId());
            Member member = memberService.findByEmail(email).orElseThrow(() -> new RuntimeException("해당 이메일의 사용자를 찾을 수 없음"));

            CategoryUser categoryUser = new CategoryUser(category,member,InviteState.INVITE, CategoryUserRole.USER, CategoryState.ACTIVE);
            CategoryUser response = categoryUserRepository.save(categoryUser);

            return ResponseEntity.ok(DefaultResponse.success(response));
        }

        return ResponseEntity.badRequest()
                .body(DefaultResponse.error(400, "가입 실패"));
    }
}
