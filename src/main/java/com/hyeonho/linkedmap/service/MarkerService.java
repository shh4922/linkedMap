package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.data.dto.marker.CreateMarkerDTO;
import com.hyeonho.linkedmap.data.request.CreateMarkerRequest;

import com.hyeonho.linkedmap.data.request.marker.UpdateMarkerRequest;
import com.hyeonho.linkedmap.entity.*;
import com.hyeonho.linkedmap.enumlist.RoomMemberRole;
import com.hyeonho.linkedmap.enumlist.InviteState;
import com.hyeonho.linkedmap.enumlist.RoomState;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.error.PermissionException;
import com.hyeonho.linkedmap.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkerService {

    private final MarkerRepository markerRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final MemberRepository memberRepository;
    private final InviteRepository inviteRepository;

    /**
     * 해당 유저가 카테고리에 속해있는지 체크
     * 유저가 Invite상태인지 체크
     * 카테고리가 Active 상태인지 체크
     * 유저가 권한이 있는지 체크 (ReadOnly x)
     *
     */
    public CreateMarkerDTO createMarker(CreateMarkerRequest req, Long memberId) {
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

       Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
               .orElseThrow(() -> new InvalidRequestException("유저없음"));

       Marker marker = Marker.builder()
               .request(req)
               .member(member)
               .room(room)
               .build();

       Marker saveMarker = markerRepository.save(marker);
       return CreateMarkerDTO.from(saveMarker);
    }

     /** 특정 카테고리의 마커개수 요청*/
    public Long getMarkerCountByRoomId(Long roomId) {
        return markerRepository.countByRoomId(roomId);
    }

    /**
     * 방에 있는 마커리스트 조회
     * @param memberId
     * @param categoryId
     * @return
     */
    public List<CreateMarkerDTO> getMarkerListByRoomId(Long memberId, Long categoryId) {
        // 카테고리가 삭제되었으면 애초에 조회가 안됌
        RoomMember roomMember = getRoomMemberByMemberIdAndRoomId(memberId, categoryId);

        // 해당 방 유저가 아닌경우. -> InviteState가 INVITE가 아닌경우
        if(!roomMember.getInviteState().equals(InviteState.INVITE)) {
            throw new PermissionException("권한이 없습니다");
        }

        List<Marker> markerList = markerRepository.getMarkerList(categoryId);

        return markerList.stream()
                .map(CreateMarkerDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CreateMarkerDTO updateMarker(Long memberId, UpdateMarkerRequest req) {
        Marker marker = findMarkerById(req.getId());

        Long creatorId = marker.getMember().getId();

        boolean isPermission = checkMarkerPermission(memberId, creatorId, marker.getRoom().getId());

        if(!isPermission) { throw new PermissionException("권한이 없습니다");}

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


    /** 카테고리 유저가 해당 카테고리에 속해있는지 체크
     * 카테고리 유저가 삭제된 상태인지 체크
     * 카테고리가 Active 상태인지 체크
     * */
    private boolean checkMarkerPermission(Long memberId, Long creatorId, Long roomId) {
        // 내가 해당 방 소속인지 체크
        RoomMember me = getRoomMemberByMemberIdAndRoomId(memberId, roomId);

        // 생성자가 해당방 소속인지 체크
        RoomMember creator = getRoomMemberByMemberIdAndRoomId(creatorId, roomId);

        RoomMemberRole myPermission = me.getRoomMemberRole();

        // 내가 방장인 경우 true
        if(myPermission.equals(RoomMemberRole.OWNER)) {
            return true;
        }

        // 내가 생성자인 경우 true
        if(me.getMember().getId().equals(creator.getMember().getId())) {
            return true;
        }

        // 내 권한이 readOnly 인 경우 false
        if(!myPermission.equals(RoomMemberRole.MANAGER)) {
            return false;
        }

        // 방장이 만든경우 false
        if(creator.getRoomMemberRole().equals(RoomMemberRole.OWNER)) {
            return false;
        }

        // 같은 매니저의 경우 필터링
        if(creator.getRoomMemberRole().equals(RoomMemberRole.MANAGER)) {
            return false;
        }

        // 나는 매니저이고, 생성자는 일반유저나, readOnly 인 경우 필터링
        return true;

    }



    /** RoomMember 를 찾는 메서드
     * RoomMember 가 존재하지 않으면 예외를 던짐
     * Room이 삭제된 상태면 예외를 던짐
     * */
    private RoomMember getRoomMemberByMemberIdAndRoomId(Long memberId, Long roomId) {
        return roomMemberRepository.getCategoryUserByEmailAndRoomId(memberId, roomId, RoomState.ACTIVE)
                .orElseThrow(() -> new InvalidRequestException("해당 카테고리유저를 찾을수 없습니다."));
    }
}
