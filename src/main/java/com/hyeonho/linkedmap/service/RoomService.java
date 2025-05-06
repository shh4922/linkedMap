package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.data.dto.category.RoomDetailDTO;
import com.hyeonho.linkedmap.data.dto.category.RoomListDTO;
import com.hyeonho.linkedmap.data.dto.member.RoomMemberDTO;
import com.hyeonho.linkedmap.data.request.room.RoomUpdateRequest;
import com.hyeonho.linkedmap.data.request.room.CreateRoomRequest;
import com.hyeonho.linkedmap.entity.Room;
import com.hyeonho.linkedmap.entity.RoomMember;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.entity.RoomState;
import com.hyeonho.linkedmap.enumlist.RoomMemberRole;
import com.hyeonho.linkedmap.enumlist.InviteState;
import com.hyeonho.linkedmap.error.DatabaseException;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.error.PermissionException;
import com.hyeonho.linkedmap.repository.*;

import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final MemberService memberService;
    private final RoomMemberRepository roomMemberRepository;
    private final MarkerService markerService;

    /**
     * 방 생성
     * 방 생성시, 생성이 완료되면 RoomMember에 invite상태로, OWNER 역할의 유저를 추가한다.
     * @param request
     * @return
     */
    public Room createRoom(Long id, CreateRoomRequest request) {
        try {
            Member member = memberService.findMemberById(id);

            Room room = new Room(member, request);
            saveRoom(room);

            // 방 생성후 RoomMember에 해당 유저 추가
            RoomMember roomMember = new RoomMember(room, member, InviteState.INVITE, RoomMemberRole.OWNER);
            roomMemberRepository.save(roomMember);
            return room;
        } catch (DatabaseException e) {
            throw new DatabaseException("카테고리 생성 에러");
        }
    }


    /** 내가 속한 방 조회 */
    public List<RoomListDTO> getMyRooms(String email) {
        List<RoomMember> roomMemberList = getIncludeRoomsByMemberId(email, InviteState.INVITE);
        return roomMemberList.stream()
                .map(roomMember -> {
                    RoomListDTO dto = RoomListDTO.fromEntityMyRoom(roomMember);

                    Long roomId = roomMember.getRoom().getId();

                    Long markerCount = markerService.getMarkerCountByRoomId(roomId);

                    dto.setMarkerCount(markerCount.intValue());

                    Long memberCount = this.getCountMemberByRoomId(roomId);
                    dto.setMemberCount(memberCount.intValue());

                    return dto;
                })
                .toList();
    }

    /** 특정 유저가 속한 방 리스트 조회*/
    public List<RoomListDTO> getRoomListById(Long memberId) {
        List<RoomMember> roomMemberList = getIncludeRoomsByMemberId(email, InviteState.INVITE);
        return roomMemberList.stream()
                .filter(roomMember -> roomMember.getRoom().getRoomState() == RoomState.ACTIVE)
                .map(roomMember -> {
                    RoomListDTO dto = RoomListDTO.fromEntityByEmail(roomMember);

                    Long roomId = roomMember.getRoom().getId();

                    Long markerCount = markerRepository.countByRoomId(roomId);
                    dto.setMarkerCount(markerCount.intValue());

                    Long memberCount = this.getCountMemberByRoomId(roomId);
                    dto.setMemberCount(memberCount.intValue());
                    return dto;
                })
                .toList();
    }


    /**
     * 방 디테일
     * 방에 속한 유저 리스트 조회
     * 방 정보 조회
     * 마커수 조회
     * @param roomId
     * @return
     */
    public RoomDetailDTO getRoomDetail(Long roomId) {
        List<RoomMember> roomMemberList = roomMemberRepository.getIncludeCategoryByRoomId(roomId, InviteState.INVITE);
        Long markerCount = markerRepository.countByRoomId(roomId);

        List<RoomMemberDTO> roomMemberDTOList = roomMemberList.stream()
                .map(categoryUser -> {
                    Member member = categoryUser.getMember();
                    return RoomMemberDTO.builder()
                            .name(member.getUsername())
                            .role(categoryUser.getRoomMemberRole().name())
                            .email(member.getEmail())
                            .inviteDate(categoryUser.getCreatedAt())
                            .build();
                })
                .toList();

        RoomMember firstRoomMember = roomMemberList.get(0);

        return RoomDetailDTO.builder()
                .roomId(firstRoomMember.getRoom().getId())
                .roomName(firstRoomMember.getRoom().getName())
                .roomDescription(firstRoomMember.getRoom().getDescription())
                .currentRoomOwnerName(firstRoomMember.getRoom().getCurrentOwner().getUsername())
                .currentRoomOwnerEmail(firstRoomMember.getRoom().getCurrentOwner().getEmail())
                .createUserEmail(firstRoomMember.getRoom().getCreator().getEmail())
                .createUserName(firstRoomMember.getRoom().getCreator().getUsername())
                .createdAt(firstRoomMember.getCreatedAt())
                .imageUrl(firstRoomMember.getRoom().getImageUrl())
                .memberList(roomMemberDTOList)
                .markerCount(markerCount.intValue())
                .build();
    }




    /**
     * 방장이 해당 방을 삭제함.
     * 방의 상태를 DELETE로 변경
     * RoomMember의 나의 inviteState를 GETOUT으로 변경
     * @param memberId
     * @param roomId
     * @return
     */
    public Room deleteMyRoom(Long memberId, Long roomId, @Nullable RoomMember roomMember) {
        Room room;

        if(roomMember != null) {
            room = roomMember.getRoom();
        } else {
            room = findRoomByRoomId(roomId);
        }

        if(!room.getCurrentOwner().getId().equals(memberId)) {
            throw new PermissionException("권한이 없습니다.");
        }

        room.delete();
        getOutRoom(memberId,roomId);
        return room;
    }

    /**
     * 방 나가기
     * RoomMember에 있는 inviteState 를 GETOUT 으로 변경
     * 마
     * @param memberId
     * @param roomId
     * @return
     */
    public int getOutRoom(Long memberId, Long roomId) {
        try {
            return roomMemberRepository.updateInviteStatusToDelete(InviteState.GETOUT, roomId, memberId);
        } catch (DatabaseException e) {
            throw new DatabaseException("예기치 못한 에러가 발생했습니다.");
        }
    }

    public Room updateRoom(String email, RoomUpdateRequest req) {
        Room room = findRoomByRoomId(req.getRoomId());
        if(!room.getCurrentOwner().getEmail().equals(email)) {
            throw new PermissionException("권한이 없습니다.");
        }

        room.update(req);
        return room;
    }


    /** 카테고리 저장 */
    public Room saveRoom(Room room) {
        try {
            return roomRepository.save(room);
        } catch (Exception e) {
            throw new DatabaseException("카테고리 정보를 저장하는데, 실패했습니다.");
        }

    }

    public Room findRoomByRoomId(Long roomId) {
        return roomRepository.findByIdAndDeletedAtIsNull(roomId)
                .orElseThrow(() -> new InvalidRequestException("해당 방을 찾을수 없습니다"));
    }


    public List<RoomMember> getIncludeRoomsByMemberId(Long memberId, InviteState inviteState) {
        return roomMemberRepository.getIncludeRoomByMemberId(memberId, inviteState);
    }

    public Long getCountMemberByRoomId(Long roomId) {
        return roomMemberRepository.countByRoomIdAndInviteState(roomId, InviteState.INVITE);
    }
}
