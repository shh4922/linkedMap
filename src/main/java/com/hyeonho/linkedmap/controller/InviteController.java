package com.hyeonho.linkedmap.controller;

import com.hyeonho.linkedmap.config.JWTProvider;
import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.data.request.room.RoomJoinRequest;
import com.hyeonho.linkedmap.data.request.InviteCreateReq;
import com.hyeonho.linkedmap.entity.*;
import com.hyeonho.linkedmap.enumlist.RoomMemberRole;
import com.hyeonho.linkedmap.enumlist.InviteState;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.repository.RoomMemberRepository;
import com.hyeonho.linkedmap.service.RoomService;
import com.hyeonho.linkedmap.service.InviteService;
import com.hyeonho.linkedmap.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final RoomMemberRepository roomMemberRepository;
    private final RoomService roomService;
    private final MemberService memberService;
    private static final Logger log = LoggerFactory.getLogger(InviteController.class);

    // TODO: 카테고리 Onwer 만 만들수 있도록 수정
    /**
     * 초대 링크 생성
     * @param req
     * @return
     */
    @PostMapping("/invite/create")
    public ResponseEntity<DefaultResponse<Map<String, String>>> createInvite(@AuthenticationPrincipal String email, @RequestBody InviteCreateReq req) {

        if(req.getCategoryId() == null) {
            throw new InvalidRequestException("categoryId 빔");
        }

        Invite invite = inviteService.createInvite(email,req.getCategoryId());
        String url = String.format("https://www.linkedmap.com/invite/%s/%s", invite.getCategoryId(), invite.getInviteKey());

        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        return ResponseEntity.ok(DefaultResponse.success(response));


    }

    /**
     * 초대링크 가입
     * @param req
     * @return
     */
    @PostMapping("/invite/join")
    public ResponseEntity<DefaultResponse<RoomMember>> joinCategory(@AuthenticationPrincipal String email, @RequestBody RoomJoinRequest req) {
        if(req.getCategoryId() == null || req.getInviteKey() == null) {
            throw new InvalidRequestException("데이터 비었음");
        }

        // 초대 uuid 가 있는지 체크
        Invite invite = inviteService.findInviteByUUID(req.getInviteKey());
        if(invite == null) {
            return ResponseEntity.ok(DefaultResponse.error(410,"없는 초대링크임"));

        }

        // Pending 인지 체크
        if(invite.getInviteState() != InviteState.PENDING) {
            return ResponseEntity.ok(DefaultResponse.error(400,"이미 가입됌"));
        }

        // 카테고리Id가 같은지 체크
        if(!invite.getCategoryId().equals(req.getCategoryId())) {
            return ResponseEntity.ok(DefaultResponse.error(400,"카테고리 Id 다름"));
        }

        // 본인이 만들고 본인이 가입하는지 체크
        if (invite.getInvitor().equals(email)) {
            throw new InvalidRequestException("본인이 들어갈수는 없음");
        }

        // 기간 만료, 해당 초대 닫고, 기한만료 전달
        if(LocalDateTime.now().isAfter(invite.getExpireAt())) {
            inviteService.updateInviteStateByUUID(invite.getInviteKey(), InviteState.TIMEOUT);
            return ResponseEntity.ok(DefaultResponse.error(405,"기한 만료"));
        }

        // inviteKey 가 같고, 파기되지 않았다면 invite 상태 업데이트
        int result = inviteService.updateInviteMemberByUUID(invite.getInviteKey(), email, InviteState.INVITE);
        if(result != 1) {
            return ResponseEntity.ok(DefaultResponse.error(500,"초대상태 업데이트 실패"));
        }

        // categoryUser에 해당유저 추가를 위해서 유저정보와, 카테고리 정보 find
        Room room = roomService.findRoomByRoomId(req.getCategoryId());
        Member member = memberService.findByEmail(email).orElseThrow(() -> new RuntimeException("해당 이메일의 사용자를 찾을 수 없음"));
        RoomMember roomMember = new RoomMember(room, member, InviteState.INVITE, RoomMemberRole.USER, RoomState.ACTIVE);
        RoomMember response = roomMemberRepository.save(roomMember);
        return ResponseEntity.ok(DefaultResponse.success(response));


//        return ResponseEntity.badRequest()
//                .body(DefaultResponse.error(400, "가입 실패"));
    }
}
