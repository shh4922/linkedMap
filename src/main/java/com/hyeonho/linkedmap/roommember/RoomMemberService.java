package com.hyeonho.linkedmap.roommember;

import com.hyeonho.linkedmap.data.request.roommember.PatchRoomMemberPermissionRequest;
import com.hyeonho.linkedmap.data.request.roommember.PostExpelledRoomMember;
import com.hyeonho.linkedmap.invite.InviteState;
import com.hyeonho.linkedmap.room.RoomState;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.error.PermissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomMemberService {

    private final RoomMemberRepository roomMemberRepository;


    public RoomMember getRoomMember(Long memberId, Long roomId, RoomState roomState) {
        return roomMemberRepository.getRoomMemberByMemberIdAndRoomId(memberId, roomId, roomState)
                .orElseThrow(() -> new InvalidRequestException("해당 유저 정보를 찾을 수 없습니다."));
    }

    public RoomMember saveRoomMember(RoomMember roomMember) {
        return roomMemberRepository.save(roomMember);
    }

    /** 권한 변경*/
    public int updateRoomMemberPermission(Long memberId, PatchRoomMemberPermissionRequest request) {
        // 요청보낸 사람 권한 확인
        RoomMember roomMember = getRoomMember(memberId, request.getRoomId(), RoomState.ACTIVE);

        if(roomMember.getRoomMemberRole() != RoomMemberRole.OWNER) {
            throw new PermissionException("권한이 없습니다.");
        }

        RoomMember targetMember = roomMemberRepository.findById(request.getRoomMemberId())
                .orElseThrow(() -> new InvalidRequestException("권한을 변경할 유저를 찾지못함"));
        return roomMemberRepository.updateRoomMemberRoleById(RoomMemberRole.valueOf(request.getPermission()), targetMember.getId());
    }

    /** roomMember 의 roomMemberState 변경*/
    public String expelledRoomMember(Long memberId, PostExpelledRoomMember request) {
        RoomMember roomMember = getRoomMember(memberId, request.getRoomId(), RoomState.ACTIVE);

        if(roomMember.getRoomMemberRole() != RoomMemberRole.OWNER) {
            throw new PermissionException("권한이 없습니다");
        }

        RoomMember targetMember = roomMemberRepository.findById(request.getRoomMemberId())
                .orElseThrow(() -> new InvalidRequestException("해당 유저를 찾을 수 없음"));
        targetMember.updateInviteState(InviteState.EXPELLED);

        if(targetMember.getInviteState() != InviteState.EXPELLED) {
            return "-1";
        } else {
            return "0";
        }
    }




    /** 특정 방에 속한 roomMember 리스트 조회 */
    public List<RoomMember> getRoomMemberListByRoomId(Long roomId, InviteState inviteState) {
        return roomMemberRepository.getRoomMemberListByRoomId(roomId, inviteState);
    }


//    public int getOutRoomByMemberIdAndRoomId(List<Long> memberIds, Long roomId) {
//        try {
//            return roomMemberRepository.updateInviteStatusToDelete(InviteState.GETOUT, roomId, memberIds);
//        } catch (DatabaseException e) {
//            throw new DatabaseException("예기치 못한 에러가 발생했습니다.");
//        }
//    }

    public Long getCountMemberByRoomId(Long roomId) {
        return roomMemberRepository.countByRoomIdAndInviteState(roomId, InviteState.INVITE);
    }



    public int updateInviteStateByMemberIdAndRoomId(InviteState inviteState, Long roomId, List<Long> memberIds) {
        return roomMemberRepository.updateInviteStatusToDelete(inviteState, roomId, memberIds);
    }

    public Long getCountRoomMemberByRoomIdAndInviteState(Long roomId, InviteState inviteState) {
        return roomMemberRepository.countByRoomIdAndInviteState(roomId, inviteState);
    }

}
