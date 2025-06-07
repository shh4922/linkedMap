package com.hyeonho.linkedmap.invite;

import com.hyeonho.linkedmap.auth.JWTProvider;
import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.helper.MemberValidationService;
import com.hyeonho.linkedmap.invite.invite.CreateInviteDTO;
import com.hyeonho.linkedmap.data.request.room.RoomJoinRequest;
import com.hyeonho.linkedmap.data.request.InviteCreateReq;
import com.hyeonho.linkedmap.roommember.RoomMemberRole;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.member.Member;
import com.hyeonho.linkedmap.roommember.RoomMember;
import com.hyeonho.linkedmap.roommember.RoomMemberRepository;
import com.hyeonho.linkedmap.room.entity.Room;
import com.hyeonho.linkedmap.room.service.RoomService;
import com.hyeonho.linkedmap.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InviteController {

    private final JWTProvider jwtProvider;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomMemberRepository memberRepository;

    private final InviteService inviteService;
    private final RoomService roomService;
    private final MemberValidationService memberValidationService;

    private static final Logger log = LoggerFactory.getLogger(InviteController.class);

    // TODO: 카테고리 Onwer 만 만들수 있도록 수정
    /**
     * 초대 링크 생성
     * @param req
     * @return
     */
    @PostMapping("/invite/create")
    public ResponseEntity<DefaultResponse<CreateInviteDTO>> createInvite(@AuthenticationPrincipal Long memberId, @RequestBody InviteCreateReq req) {
        if(req.getRoomId() == null) {
            throw new InvalidRequestException("Room id is required");
        }
        return ResponseEntity.ok(DefaultResponse.success(inviteService.createInvite(memberId,req.getRoomId())));
    }

    /**
     * 초대링크 가입
     * @param req
     * @return
     */
    @PostMapping("/invite/join")
    public ResponseEntity<DefaultResponse<String>> joinCategory(@AuthenticationPrincipal Long memberId, @RequestBody RoomJoinRequest req) {
        if(req.getRoomId() == null || req.getInviteKey() == null) {
            throw new InvalidRequestException("데이터 비었음");
        }

        return ResponseEntity.ok(DefaultResponse.success(inviteService.joinRoom(memberId, req)));
    }

    @GetMapping("/invite/check/{key}")
    public ResponseEntity<DefaultResponse<String>> checkInviteKey(@PathVariable("key") String key) {
        return ResponseEntity.ok(DefaultResponse.success(inviteService.checkInviteKey(key)));
    }
}
