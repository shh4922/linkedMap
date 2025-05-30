package com.hyeonho.linkedmap.room.service;

import com.hyeonho.linkedmap.room.category.RoomDetailDTO;
import com.hyeonho.linkedmap.room.category.RoomListDTO;
import com.hyeonho.linkedmap.member.member.RoomMemberDTO;
import com.hyeonho.linkedmap.data.request.room.RoomUpdateRequest;
import com.hyeonho.linkedmap.data.request.room.CreateRoomRequest;
import com.hyeonho.linkedmap.member.MemberRepository;
import com.hyeonho.linkedmap.room.entity.Room;
import com.hyeonho.linkedmap.roommember.RoomMember;
import com.hyeonho.linkedmap.member.Member;
import com.hyeonho.linkedmap.room.RoomState;
import com.hyeonho.linkedmap.roommember.RoomMemberRole;
import com.hyeonho.linkedmap.invite.InviteState;
import com.hyeonho.linkedmap.error.DatabaseException;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.error.PermissionException;

import com.hyeonho.linkedmap.room.repository.RoomRepository;
import com.hyeonho.linkedmap.marker.MarkerService;
import com.hyeonho.linkedmap.roommember.RoomMemberRepository;
import com.hyeonho.linkedmap.s3.S3Service;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final MarkerService markerService;
    private final S3Service s3Service;

    /**
     * 방 생성
     * 방 생성시, 생성이 완료되면 RoomMember에 invite상태로, OWNER 역할의 유저를 추가한다.
     * @param request
     * @return
     */
    public Room createRoom(Long memberId, CreateRoomRequest request) {
        try {
            Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                    .orElseThrow(() -> new InvalidRequestException("없는 유저입니다."));


            Room room = new Room(member, request);
            saveRoom(room);

//            if(request.getImageUrl() != null) {
//                String key = request.getImageUrl();
//                String dummyUrl = s3Service.generateUploadUrl(key,request.getContentType());
//
//            }

            // 방 생성후 RoomMember에 해당 유저 추가
            RoomMember roomMember = new RoomMember(room, member, InviteState.INVITE, RoomMemberRole.OWNER);
            roomMemberRepository.save(roomMember);
            return room;
        } catch (DatabaseException e) {
            throw new DatabaseException("카테고리 생성 에러");
        }
    }


    /**
     * 내가 속한 방 리스트 조회
     * 내가 속한 방은 방장에 의해 삭제된 방도 보여줘야 함.
     * 그래서 InviteState 가 active 인 것을 조회하고, roomState가 delete는, active는 모두 조회후
     * delete 상태의 방은, 방 나가기를 화면에서 유도해야한다.
     * */
    public List<RoomListDTO> getMyRooms(Long memberId) {
        memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new InvalidRequestException("없는 유저입니다."));

        List<RoomMember> roomMemberList = getRoomMemberListByMemberId(memberId, InviteState.INVITE);

        return getRoomListDTO(roomMemberList);

    }


    /**
     * 특정 유저가 속한 방 리스트 조회
     * 다른 유저가 속한 방 리스트의 경우, 삭제된 방의 정보는 표시하지 않아야함.
     * 그래서 filter 를 걸어 roomState가 delete 는 필터링 후 리턴
     * @param memberId
     * @return
     */
    public List<RoomListDTO> getRoomListById(Long memberId) {
        List<RoomMember> roomMemberList = getRoomMemberListByMemberId(memberId, InviteState.INVITE);
        return getRoomListDTO(roomMemberList).stream()
                .filter(roomListDTO -> {
                    return !Objects.equals(roomListDTO.getRoomState(), RoomState.DELETE.name());
                })
                .toList();
    }


    /**
     * 방 디테일 조회
     * 방에 속한 마커의 수
     * 방에 참가한 유저정보 (id, name, email ...)
     * 권한, roomMemberId, 초대 된 날짜,
     */
    public RoomDetailDTO getRoomDetail(Long roomId) {
        List<RoomMember> roomMemberList = roomMemberRepository.getRoomMemberListByRoomId(roomId, InviteState.INVITE);
        Long markerCount = markerService.getMarkerCountByRoomId(roomId);


        List<RoomMemberDTO> roomMemberDTOList = roomMemberList.stream()
                .map(roomMember -> {
                    Member member = roomMember.getMember();
                    return RoomMemberDTO.builder()
                            .roomMemberId(roomMember.getId())
                            .memberId(member.getId())
                            .name(member.getUsername())
                            .role(roomMember.getRoomMemberRole().name())
                            .email(member.getEmail())
                            .inviteDate(roomMember.getInviteAt())
                            .build();
                })
                .toList();

        RoomMember firstRoomMember = roomMemberList.get(0);

        return RoomDetailDTO.builder()
                .roomId(firstRoomMember.getRoom().getId())
                .roomName(firstRoomMember.getRoom().getName())
                .roomDescription(firstRoomMember.getRoom().getDescription())
                .currentRoomOwnerId(firstRoomMember.getRoom().getCurrentOwner().getId())
                .currentRoomOwnerName(firstRoomMember.getRoom().getCurrentOwner().getUsername())
                .currentRoomOwnerEmail(firstRoomMember.getRoom().getCurrentOwner().getEmail())
                .createUserEmail(firstRoomMember.getRoom().getCreator().getEmail())
                .createUserName(firstRoomMember.getRoom().getCreator().getUsername())
                .createdAt(firstRoomMember.getInviteAt())
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

    public String updateRoom(Long memberId, RoomUpdateRequest req) {
        Room room = findRoomByRoomId(req.getRoomId());
        if(!room.getCurrentOwner().getId().equals(memberId)) {
            throw new PermissionException("권한이 없습니다.");
        }

        room.update(req);
        return "방 정보가 수정되었습니다.";
    }


    /** 방 저장 */
    public Room saveRoom(Room room) {
        try {
            return roomRepository.save(room);
        } catch (Exception e) {
            throw new DatabaseException("방 정보를 저장하는데, 실패했습니다.");
        }

    }

    public Room findRoomByRoomId(Long roomId) {
        return roomRepository.findByIdAndDeletedAtIsNull(roomId)
                .orElseThrow(() -> new InvalidRequestException("해당 방을 찾을수 없습니다"));
    }


    public List<RoomMember> getRoomMemberListByMemberId(Long memberId, InviteState inviteState) {
        return roomMemberRepository.getRoomMemberListByMemberId(memberId, inviteState);
    }

    public Long getCountMemberByRoomId(Long roomId) {
        return roomMemberRepository.countByRoomIdAndInviteState(roomId, InviteState.INVITE);
    }

    private List<RoomListDTO> getRoomListDTO(List<RoomMember> roomMemberList) {
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
}
