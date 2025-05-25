package com.hyeonho.linkedmap.roommember;

import com.hyeonho.linkedmap.data.request.roommember.GetRoomMemberRequest;
import com.hyeonho.linkedmap.data.request.roommember.PatchRoomMemberPermissionRequest;
import com.hyeonho.linkedmap.data.request.roommember.PostExpelledRoomMember;
import com.hyeonho.linkedmap.invite.InviteState;
import com.hyeonho.linkedmap.room.RoomState;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.error.PermissionException;
import com.hyeonho.linkedmap.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomMemberService {

    private final RoomMemberRepository roomMemberRepository;
    private final RoomRepository roomRepository;


    public RoomMember getRoomMember(GetRoomMemberRequest request) {
        return roomMemberRepository.getRoomMemberByMemberIdAndRoomId(request.getMemberId(), request.getRoomId(), RoomState.ACTIVE)
                .orElseThrow(() -> new InvalidRequestException("해당 유저 정보를 찾을 수 없습니다."));
    }

    /** 권한 변경*/
    public int updateRoomMemberPermission(Long memberId, PatchRoomMemberPermissionRequest request) {
        // 요청보낸 사람 권한 확인
        RoomMember roomMember = roomMemberRepository.getRoomMemberByMemberIdAndRoomId(memberId, request.getRoomId(), RoomState.ACTIVE)
                .orElseThrow(() -> new InvalidRequestException("요청 보낸 사람의 정보가 없음"));

        if(roomMember.getRoomMemberRole() != RoomMemberRole.OWNER) {
            throw new PermissionException("권한이 없습니다.");
        }




        RoomMember targetMember = roomMemberRepository.findById(request.getRoomMemberId())
                .orElseThrow(() -> new InvalidRequestException("권한을 변경할 유저를 찾지못함"));

        return roomMemberRepository.updateRoomMemberRoleById(RoomMemberRole.valueOf(request.getPermission()), targetMember.getId());
    }

    /** roomMember 의 roomMemberState 변경*/
    public String expelledRoomMember(Long memberId, PostExpelledRoomMember request) {
        RoomMember roomMember = roomMemberRepository.getRoomMemberByMemberIdAndRoomId(memberId, request.getRoomId(), RoomState.ACTIVE)
                .orElseThrow(() -> new InvalidRequestException("요청 보낸 사람의 정보가 없음"));

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
}
