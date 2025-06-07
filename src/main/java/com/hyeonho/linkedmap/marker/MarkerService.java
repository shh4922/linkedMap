package com.hyeonho.linkedmap.marker;

import com.hyeonho.linkedmap.helper.MemberValidationService;
import com.hyeonho.linkedmap.marker.marker.CreateMarkerDTO;
import com.hyeonho.linkedmap.data.request.CreateMarkerRequest;

import com.hyeonho.linkedmap.data.request.marker.UpdateMarkerRequest;
import com.hyeonho.linkedmap.roommember.RoomMemberRole;
import com.hyeonho.linkedmap.invite.InviteState;
import com.hyeonho.linkedmap.room.RoomState;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.error.PermissionException;
import com.hyeonho.linkedmap.invite.InviteRepository;
import com.hyeonho.linkedmap.member.Member;
import com.hyeonho.linkedmap.member.MemberRepository;
import com.hyeonho.linkedmap.room.entity.Room;
import com.hyeonho.linkedmap.room.repository.RoomRepository;
import com.hyeonho.linkedmap.roommember.RoomMember;
import com.hyeonho.linkedmap.roommember.RoomMemberRepository;
import com.hyeonho.linkedmap.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkerService {

    private final MarkerRepository markerRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final MemberValidationService memberValidationService;
//    private final MemberRepository memberRepository;
    private final InviteRepository inviteRepository;
    private final MarkerQueryRepository markerQueryRepository;
    private final S3Service s3Service;

    /**
     * 해당 유저가 카테고리에 속해있는지 체크
     * 유저가 Invite상태인지 체크
     * 카테고리가 Active 상태인지 체크
     * 유저가 권한이 있는지 체크 (ReadOnly x)
     *
     */
    public CreateMarkerDTO createMarker( Long memberId, CreateMarkerRequest req, Optional<MultipartFile> file) {
       RoomMember roomMember = getRoomMemberByMemberIdAndRoomId(memberId, req.getRoomId());

       // 초대되지 않은 유저인 경우
       if(!roomMember.getInviteState().equals(InviteState.INVITE)) {
           throw new PermissionException("권한이 없습니다");
       }

       // 생성 권한이 없는경우
       if(roomMember.getRoomMemberRole().equals(RoomMemberRole.READ_ONLY)) { // 403
           throw new PermissionException("권한이 없습니다");
       }

       // 화면단에서 roomId 잘못 보냈을떄
       Room room = roomRepository.findById(req.getRoomId())
               .orElseThrow(() -> new InvalidRequestException("존재하지 않는 방입니다"));

       Member member = memberValidationService.findMemberById(memberId);

       String imageUrl = null;
       if(file.isPresent()) {
           imageUrl = this.getUploadImageUrl(file.get(), room);
       }

       Marker marker = Marker.builder()
               .request(req)
               .member(member)
               .room(room)
               .imageUrl(imageUrl)
               .build();

       Marker saveMarker = markerRepository.save(marker);
       return CreateMarkerDTO.from(saveMarker);
    }

     /** 특정 카테고리의 마커개수 요청*/
    public Long getMarkerCountByRoomId(Long roomId) {
        return markerRepository.countByRoomIdAndDeletedAtIsNull(roomId);
    }

    /** 방에 있는 마커리스트 조회 */
    public List<CreateMarkerDTO> getMarkerListByRoomId(Long memberId, Long roomId) {
        // 방이 삭제되었으면 애초에 조회가 안됌
        RoomMember roomMember = getRoomMemberByMemberIdAndRoomId(memberId, roomId);

        // 해당 방 유저가 아닌경우. -> InviteState가 INVITE가 아닌경우
        if(!roomMember.getInviteState().equals(InviteState.INVITE)) {
            throw new PermissionException("권한이 없습니다");
        }
        return markerQueryRepository.getMarkerList(memberId, roomId);
    }

    @Transactional
    public CreateMarkerDTO updateMarker(Long memberId, UpdateMarkerRequest req, Optional<MultipartFile> file) {
        Marker marker = findMarkerById(req.getMarkerId());

        Long creatorId = marker.getMember().getId();

        boolean isPermission = checkMarkerPermission(memberId, creatorId, marker.getRoom().getId());

        if(!isPermission) { throw new PermissionException("권한이 없습니다");}

        if(file.isPresent()) {
            String imageUrl = this.getUploadImageUrl(file.get(), marker.getRoom());
            marker.uploadImage(imageUrl);
        }
        marker.update(req);

        return CreateMarkerDTO.from(marker);
    }

    @Transactional
    public Marker deleteMarker(Long memberId, Long markerId) {
        Marker marker = findMarkerById(markerId);
        Long creatorId = marker.getMember().getId();
        Long roomId = marker.getRoom().getId();

        boolean isPermission = checkMarkerPermission(memberId, creatorId, roomId);

        if(!isPermission) { throw new PermissionException("권한이 없습니다");}

        marker.delete();
        return marker;
    }

    /** 마커를 찾는 메서드
     * 마커가 존재하지 않으면 예외를 던짐
     * 마커가 삭제된 상태면 예외를 던짐
     * */
    private Marker findMarkerById(Long markerId) {
        return markerRepository.findByIdAndDeletedAtIsNull(markerId)
                .orElseThrow(() -> new InvalidRequestException("해당Id의 마커를 찾을수 없음"));
    }



    /**
     * @param myId 방 유저의 아이디
     * @param creatorId 마커를 생성한 유저의 아이디
     * @param roomId 마커가 속한 방의 아이디
     * @return true: 권한 있음, false: 권한 없음
     */
    private boolean checkMarkerPermission(Long myId, Long creatorId, Long roomId) {
        // 내가 해당 방 소속인지 체크
        RoomMember me = getRoomMemberByMemberIdAndRoomId(myId, roomId);

        // 생성자가 해당방 소속인지 체크
        RoomMember creator = getRoomMemberByMemberIdAndRoomId(creatorId, roomId);

        RoomMemberRole myPermission = me.getRoomMemberRole();

        // 내가 마커 생성자인 경우 경우 true
        if(myId.equals(creatorId)) return true;

        // 내가 방장인 경우 true
        if(myPermission.equals(RoomMemberRole.OWNER)) return true;

        // 방장이 만든경우 false
        if(creator.getRoomMemberRole().equals(RoomMemberRole.OWNER))  return false;

        // 나는 매니저, 일반유저, readOnly 인 경우 true
        if(myPermission.equals(RoomMemberRole.MANAGER) &&
                (creator.getRoomMemberRole().equals(RoomMemberRole.USER)|| creator.getRoomMemberRole().equals(RoomMemberRole.READ_ONLY))
        ) return true;

        return true;
    }



    /** RoomMember 를 찾는 메서드
     * RoomMember 가 존재하지 않으면 예외를 던짐
     * Room이 삭제된 상태면 예외를 던짐
     * */
    private RoomMember getRoomMemberByMemberIdAndRoomId(Long memberId, Long roomId) {
        return roomMemberRepository.getRoomMemberByMemberIdAndRoomId(memberId, roomId, RoomState.ACTIVE)
                .orElseThrow(() -> new InvalidRequestException("해당 카테고리유저를 찾을수 없습니다."));
    }

    private String getUploadImageUrl(MultipartFile file, Room room) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String s3Key = "markers/" + room.getId() + "/" + timestamp + ".jpeg";
        return  s3Service.upload(file, s3Key);
    }

}
