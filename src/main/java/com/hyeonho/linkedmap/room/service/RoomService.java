package com.hyeonho.linkedmap.room.service;

import com.hyeonho.linkedmap.helper.MemberValidationService;
import com.hyeonho.linkedmap.room.data.RoomDetailDTO;
import com.hyeonho.linkedmap.room.data.RoomListDTO;
import com.hyeonho.linkedmap.member.member.RoomMemberDTO;
import com.hyeonho.linkedmap.data.request.room.RoomUpdateRequest;
import com.hyeonho.linkedmap.data.request.room.CreateRoomRequest;
import com.hyeonho.linkedmap.room.entity.Room;
import com.hyeonho.linkedmap.roommember.*;
import com.hyeonho.linkedmap.member.Member;
import com.hyeonho.linkedmap.room.RoomState;
import com.hyeonho.linkedmap.invite.InviteState;
import com.hyeonho.linkedmap.error.DatabaseException;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.error.PermissionException;

import com.hyeonho.linkedmap.room.repository.RoomRepository;
import com.hyeonho.linkedmap.marker.MarkerService;
import com.hyeonho.linkedmap.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomMemberQueryRepository roomMemberQueryRepository;
    private final RoomMemberService roomMemberService;

    private final S3Service s3Service;
    private final MemberValidationService memberValidationService;
    private final MarkerService markerService;



    /** 방 생성*/
    public Room createRoom(Long memberId,
                           @RequestPart("image") Optional<MultipartFile> file,
                           @RequestPart("dto") CreateRoomRequest request) {
        try {
            Member member = memberValidationService.findMemberById(memberId);

            Room room = new Room(member, request);
            saveRoom(room);

            if(file.isPresent()) {
                String url = this.getUploadImageUrl(file.get(), room);
                room.uploadImage(url);
            }

            // 방 생성후 RoomMember에 해당 유저 추가
            RoomMember roomMember = new RoomMember(room, member, InviteState.INVITE, RoomMemberRole.OWNER);
            roomMemberRepository.save(roomMember);

            return room;
        } catch (DatabaseException e) {
            throw new DatabaseException("방 생성 에러");
        }
    }


    /** 내가 속한 방 리스트 조회 */
    public List<RoomListDTO> getMyRooms(Long memberId) {
        return roomMemberQueryRepository.findMyRoomListWithCounts(memberId);

    }


    /** 특정 유저가 속한 방 리스트 조회 - 삭제된 방은 필터링 해서 보여줘야함.*/
    public List<RoomListDTO> getRoomListById(Long memberId) {

        List<RoomListDTO> roomMemberList = roomMemberQueryRepository.findMyRoomListWithCounts(memberId);
        return roomMemberList.stream()
                .filter(roomListDTO -> {
                    return !Objects.equals(roomListDTO.getRoomState(), RoomState.DELETE.name());
                })
                .toList();
        //        List<RoomMember> roomMemberList = roomMemberRepository.getRoomMemberListByMemberId(memberId,InviteState.INVITE);
//        return getRoomListDTO(roomMemberList).stream()
//                .filter(roomListDTO -> {
//                    return !Objects.equals(roomListDTO.getRoomState(), RoomState.DELETE.name());
//                })
//                .toList();
    }


    /** 방 디테일 조회
     * 방 정보 조회
     * 마커개수 조회.
     * 방에속한 유저 리스트 조회
     * */
    public RoomDetailDTO getRoomDetail(Long memberId, Long roomId) {
        RoomDetailDTO roomDetailDTO = roomMemberQueryRepository.findRoomDetailById(memberId, roomId);
        if(roomDetailDTO == null) {
            throw new InvalidRequestException("해당 방의 정보가 없습니다");
        }
        List<RoomMember> roomMemberList = roomMemberRepository.getRoomMemberListByRoomId(roomId, InviteState.INVITE);

        List<RoomMemberDTO> roomMemberDTOList =  roomMemberList.stream()
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
                }).toList();

        roomDetailDTO.setMembers(roomMemberDTOList);
        return roomDetailDTO;
    }


    /** 방삭제 by roomId*/
    public Room deleteRoomByRoomId(Long memberId, Long roomId) {
        Room room = findRoomByRoomId(roomId);
        if(!room.getCurrentOwner().getId().equals(memberId)) {
            throw new PermissionException("권한이 없습니다.");
        }

        // 방 삭제후, 자신의 RoomMember 상태를 GETOUT 으로 변경
        room.delete();
        roomMemberService.updateInviteStateByMemberIdAndRoomId(InviteState.GETOUT, room.getId(), List.of(memberId));
        return room;
    }

    // TODO: 회원탈퇴시, 탈퇴한 유저가 방장인 방을 모두 삭제 로직 추가해야함.
    public int deleteRoomByMemberId(Long memberId) {
        return roomRepository.updateRoomStateByMemberId(RoomState.DELETE, memberId);
    }



    public String updateRoom(Long memberId, Optional<MultipartFile> file, RoomUpdateRequest req) {
        Room room = findRoomByRoomId(req.getRoomId());
        if(!room.getCurrentOwner().getId().equals(memberId)) {
            throw new PermissionException("권한이 없습니다.");
        }

        if(file.isPresent()) {
            String url = this.getUploadImageUrl(file.get(), room);
            room.uploadImage(url);
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


    /** 방 나가기 */
    public int getOutRoomByMemberIdAndRoomId(List<Long> memberIds, Long roomId) {
        return roomMemberRepository.updateInviteStatusToDelete(InviteState.GETOUT, roomId, memberIds);
    }

    /** 방 조회 */
    public Room findRoomByRoomId(Long roomId) {
        return roomRepository.findByIdAndDeletedAtIsNull(roomId)
                .orElseThrow(() -> new InvalidRequestException("해당 방을 찾을수 없습니다"));
    }

    /** 특정 유저가 속한 방 리스트 조회 */
    private List<RoomListDTO> getRoomListDTO(List<RoomMember> roomMemberList) {
        return roomMemberList.stream()
                .map(roomMember -> {
                    RoomListDTO dto = RoomListDTO.fromEntityMyRoom(roomMember);

                    Long roomId = roomMember.getRoom().getId();

                    Long markerCount = markerService.getMarkerCountByRoomId(roomId);
                    dto.setMarkerCount(markerCount.intValue());

                    Long memberCount = roomMemberService.getCountRoomMemberByRoomIdAndInviteState(roomId, InviteState.INVITE);
                    dto.setMemberCount(memberCount.intValue());

                    return dto;
                })
                .toList();
    }

    private String getUploadImageUrl(MultipartFile file, Room room) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String s3Key = "rooms/" + room.getId() + "/" + timestamp + ".jpeg";
        return  s3Service.upload(file, s3Key);
    }

}
