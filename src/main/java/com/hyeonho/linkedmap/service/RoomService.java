package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.data.dto.category.RoomDetailDTO;
import com.hyeonho.linkedmap.data.dto.member.RoomMemberDTO;
import com.hyeonho.linkedmap.data.request.room.RoomUpdateRequest;
import com.hyeonho.linkedmap.data.request.room.CreateRoomRequest;
import com.hyeonho.linkedmap.entity.Room;
import com.hyeonho.linkedmap.entity.RoomMember;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.enumlist.RoomMemberRole;
import com.hyeonho.linkedmap.enumlist.InviteState;
import com.hyeonho.linkedmap.error.DatabaseException;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.error.PermissionException;
import com.hyeonho.linkedmap.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final MarkerRepository markerRepository;

    /**
     * 방 생성
     * 방 생성시, 생성이 완료되면 RoomMember에 invite상태로, OWNER 역할의 유저를 추가한다.
     * @param request
     * @return
     */
    public Room createRoom(String email, CreateRoomRequest request) {
        try {
            Member member = memberRepository.findById(email)
                    .orElseThrow(() -> new RuntimeException("해당 계정 없음"));

            Room room = new Room(member, request);
            saveRoom(room);

            // 카테고리 생성후, 카테고리유저 테이블에 추가
            RoomMember roomMember = new RoomMember(room, member, InviteState.INVITE, RoomMemberRole.OWNER);
            roomMemberRepository.save(roomMember);
            return room;
        } catch (DatabaseException e) {
            throw new DatabaseException("카테고리 생성 에러");
        }
    }

    /**
     * 방 디테일
     * 방에 속한 유저 리스트 조회
     * 방 정보 조회
     * 마커수 조회
     * @param email
     * @param roomId
     * @return
     */
    public RoomDetailDTO getRoomDetail(String email, Long roomId) {
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
                .categoryId(firstRoomMember.getRoom().getId())
                .categoryName(firstRoomMember.getRoom().getName())
                .owner(firstRoomMember.getRoom().getCurrentOwner().getUsername())
                .description(firstRoomMember.getRoom().getDescription())
                .createdAt(firstRoomMember.getRoom().getCreatedAt())
                .memberList(roomMemberDTOList)
                .markerCount(markerCount.intValue())
                .build();
    }




    /**
     * 자신의 방 삭제
     * 삭제시, 방에 속한 유저들은 방 상태가 delete로 바뀜
     * 본은은
     * @param email
     * @param roomId
     * @return
     */
    public Room deleteRoom(String email, Long roomId) {
        try {
            Room room = findRoomByRoomId(roomId);

            if(!room.getCurrentOwner().getEmail().equals(email)) {
                throw new PermissionException("권한이 없습니다.");
            }

            room.delete();
            getOutRoom(email,roomId);
            return room;
        } catch (DatabaseException e) {
            throw new DatabaseException("예기치 못한 에러가 발생했습니다.");
        }
    }

    /**
     * 방 나가기
     * RoomMember에 있는 inviteState 를 GETOUT 으로 변경
     * @param email
     * @param roomId
     * @return
     */
    public int getOutRoom(String email, Long roomId) {
        try {
            return roomMemberRepository.updateInviteStatusToDelete(InviteState.GETOUT, roomId, email);
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
        return roomRepository.findByIdAndDeletedAtIsNull(roomId);
    }


    public List<RoomMember> getIncludeRoomsByEmail(String email, InviteState inviteState) {
        return roomMemberRepository.getIncludeRoomByEmail(email, inviteState);
    }

    public Long getCountMemberByRoomId(Long roomId) {
        return roomMemberRepository.countByRoomIdAndInviteState(roomId, InviteState.INVITE);
    }
}
