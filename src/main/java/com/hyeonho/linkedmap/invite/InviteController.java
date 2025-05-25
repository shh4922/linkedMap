package com.hyeonho.linkedmap.invite;

import com.hyeonho.linkedmap.auth.JWTProvider;
import com.hyeonho.linkedmap.data.DefaultResponse;
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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InviteController {

    private final JWTProvider jwtProvider;
    private final InviteService inviteService;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomService roomService;
    private final MemberService memberService;
    private final RoomMemberRepository memberRepository;
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
    public ResponseEntity<DefaultResponse<RoomMember>> joinCategory(@AuthenticationPrincipal Long memberId, @RequestBody RoomJoinRequest req) {
        if(req.getRoomId() == null || req.getInviteKey() == null) {
            throw new InvalidRequestException("데이터 비었음");
        }

        // 초대 uuid 가 있는지 체크
        Invite invite = inviteService.findInviteByUUID(req.getInviteKey());

        if(invite == null) {
            return ResponseEntity.ok(DefaultResponse.error(410,"없는 초대링크임"));
        }

        // 기간 만료, 해당 초대 닫고, 기한만료 전달
        if(LocalDateTime.now().isAfter(invite.getExpireAt())) {
            inviteService.updateInviteStateByUUID(invite.getInviteKey(), InviteState.TIMEOUT);
            return ResponseEntity.ok(DefaultResponse.error(405,"초대 기한이 만료되었습니다"));
        }

        // 본인이 만들고 본인이 가입하는지 체크
        if (invite.getInvitor().equals(memberId)) {
            return ResponseEntity.ok(DefaultResponse.error(406,"잘못된 요청입니다"));
        }

        // Pending 인지 체크
        if(invite.getInviteState() != InviteState.PENDING) {
            return ResponseEntity.ok(DefaultResponse.error(400,"이미 가입됌"));
        }

        // 방 Id 가 같은지 체크
        if(!invite.getRoomId().equals(req.getRoomId())) {
            return ResponseEntity.ok(DefaultResponse.error(400,"방 정보가 다름"));
        }

        // 이미 초대된 유저 체크
        RoomMember roomMember = roomMemberRepository.findByRoomIdAndMemberIdAndInviteState(invite.getRoomId(), memberId, InviteState.INVITE);
        if(roomMember != null) {
            return ResponseEntity.ok(DefaultResponse.error(407,"이미 가입된 유저입니다"));
        }


        // inviteKey 가 같고, 파기되지 않았다면 invite 상태 업데이트
        int result = inviteService.updateInviteMemberByUUID(invite.getInviteKey(), memberId, InviteState.INVITE);
        if(result != 1) {
            return ResponseEntity.ok(DefaultResponse.error(500,"초대상태 업데이트 실패"));
        }

        // categoryUser에 해당유저 추가를 위해서 유저정보와, 카테고리 정보 find
        Room room = roomService.findRoomByRoomId(req.getRoomId());

        Member member = memberService.findMemberById(memberId);

        RoomMember roomMember2 = RoomMember.builder()
                .room(room)
                .member(member)
                .inviteState(InviteState.INVITE)
                .roomMemberRole(RoomMemberRole.USER)
                .build();

        RoomMember response = roomMemberRepository.save(roomMember2);
        return ResponseEntity.ok(DefaultResponse.success(response));

    }
}
