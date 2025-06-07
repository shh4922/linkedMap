package com.hyeonho.linkedmap.invite;

import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.data.request.room.RoomJoinRequest;
import com.hyeonho.linkedmap.helper.MemberValidationService;
import com.hyeonho.linkedmap.invite.invite.CreateInviteDTO;
import com.hyeonho.linkedmap.member.Member;
import com.hyeonho.linkedmap.room.entity.Room;
import com.hyeonho.linkedmap.error.DatabaseException;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.room.repository.RoomRepository;
import com.hyeonho.linkedmap.room.service.RoomService;
import com.hyeonho.linkedmap.roommember.RoomMember;
import com.hyeonho.linkedmap.roommember.RoomMemberRepository;
import com.hyeonho.linkedmap.roommember.RoomMemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InviteService {

    private final InviteRepository inviteRepository;
    private final RoomService roomService;
    private final RoomMemberRepository roomMemberRepository;
    private final MemberValidationService memberValidationService;

    // TODO: 매니저도 링크를 만들수있게 해야할까?
    public CreateInviteDTO createInvite(Long memberId, Long roomId) {
        try {
            Room room = roomService.findRoomByRoomId(roomId);

            if(!room.getCurrentOwner().getId().equals(memberId)) {
                throw new InvalidRequestException("권한이 없습니다");
            }

            Invite invite = Invite
                    .builder()
                    .roomId(roomId)
                    .invitor(memberId)
                    .build();
            Invite invite1 = inviteRepository.save(invite);

            String url = String.format("https://www.linkedmap.com/invite/%s/%s", invite1.getRoomId(), invite.getInviteKey());
            return new CreateInviteDTO(url);

        }catch (DatabaseException e) {
            throw new DatabaseException(e.getMessage());
        }
    }


    public String joinRoom(Long memberId, RoomJoinRequest req) {
        try {
            // 초대 uuid 가 있는지 체크
            Invite invite = findInviteByUUID(req.getInviteKey());

            if(invite == null) {
                throw new InvalidRequestException("잘못된 요청입니다");
            }

            // 기간 만료, 해당 초대 닫고, 기한만료 전달
            if(LocalDateTime.now().isAfter(invite.getExpireAt())) {
                inviteRepository.updateInviteStateByUUID(InviteState.TIMEOUT, invite.getInviteKey());
                throw new InvalidRequestException("초대 기한이 만료되었습니다");
            }

            // 본인이 만들고 본인이 가입하는지 체크
            if (invite.getInvitor().equals(memberId)) {
                throw new InvalidRequestException("본인이 만든 초대 링크로는 가입할 수 없습니다");
            }

            // Pending 인지 체크
            if(invite.getInviteState() != InviteState.PENDING) {
                throw new InvalidRequestException("초대 상태가 PENDING이 아닙니다");
            }

            // 방 Id 가 같은지 체크
            if(!invite.getRoomId().equals(req.getRoomId())) {
                throw new InvalidRequestException("초대 링크와 방 ID가 일치하지 않습니다");
            }

            // 이미 초대된 유저 체크 & 추방된 유저 체크
            RoomMember roomMember = roomMemberRepository.findByRoomIdAndMemberId(invite.getRoomId(), memberId);
            if(roomMember != null) {
                if(roomMember.getInviteState().equals(InviteState.EXPELLED)) {
                    throw new InvalidRequestException("추방된 유저입니다");
                }
                if(roomMember.getInviteState().equals(InviteState.INVITE)) {
                    throw new InvalidRequestException("이미 초대된 유저입니다");
                }
            }

            // inviteKey 가 같고, 파기되지 않았다면 invite 상태 업데이트
            int result = inviteRepository.updateInviteMemberByUUID(InviteState.INVITE, memberId, invite.getInviteKey());
            if(result != 1) {
                throw new DatabaseException("초대상태 업데이트 실패");
            }

            // RoomMember 에 해당유저 추가를 위해서 Member, Room 정보 find
            Room room = roomService.findRoomByRoomId(req.getRoomId());
            Member member = memberValidationService.findMemberById(memberId);

            RoomMember roomMember2 = RoomMember.builder()
                    .room(room)
                    .member(member)
                    .inviteState(InviteState.INVITE)
                    .roomMemberRole(RoomMemberRole.USER)
                    .build();
            roomMemberRepository.save(roomMember2);

            return "0";
        } catch (DatabaseException e) {
            throw new DatabaseException("방 가입 에러");
        }


    }

    public Invite findInviteByUUID(UUID inviteKey) {
        return inviteRepository.findInviteByUUID(inviteKey)
                .orElseThrow(() -> new InvalidRequestException("존재하지 않는 초대 링크입니다"));
    }

    public String checkInviteKey(String key) {
        UUID inviteKey;
        try {
            inviteKey = UUID.fromString(key);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("초대코드 형식이 잘못되었습니다.");  // 커스텀 메시지로
        }

        Invite invite = inviteRepository.findInviteByUUID(inviteKey)
                .orElseThrow(() -> new InvalidRequestException("잘못된 초대 링크입니다."));

        if (invite.getInviteState() == InviteState.TIMEOUT) {
            throw new InvalidRequestException("초대 기한이 만료되었습니다.");
        }

        if(invite.getInviteState() == InviteState.INVITE) {
            throw new InvalidRequestException("이미 사용한 링크입니다.");
        }

        return "0";
    }

}
