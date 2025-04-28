package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.data.dto.marker.CreateMarkerDTO;
import com.hyeonho.linkedmap.data.request.CreateMarkerRequest;

import com.hyeonho.linkedmap.data.request.marker.UpdateMarkerRequest;
import com.hyeonho.linkedmap.entity.*;
import com.hyeonho.linkedmap.enumlist.RoomMemberRole;
import com.hyeonho.linkedmap.enumlist.InviteState;
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
    public CreateMarkerDTO createMarker(CreateMarkerRequest req, String email) {
       RoomMember roomMember = getCategoryUserByEmailAndCategoryId(email, req.getCategoryId());

       /** 추방당함 또는 카테고리 를 나간경우 */
       if(!roomMember.getInviteState().equals(InviteState.INVITE)) {
           throw new PermissionException("권한이 없습니다");
       }

       /** 권한이 없는경우 */
       if(roomMember.getRoomMemberRole().equals(RoomMemberRole.READ_ONLY)) { //403
           throw new PermissionException("권한이 없습니다");
       }

       Room room = roomRepository.findById(req.getCategoryId()) // 403
               .orElseThrow(() -> new InvalidRequestException("해당 카테고리가 존재하지않습니다"));

       Member member = memberRepository.findById(email)
               .orElseThrow(() -> new InvalidRequestException("유저없음"));

       Marker marker = Marker.builder()
               .request(req)
               .member(member)
               .category(room)
               .build();

       markerRepository.save(marker);
       return CreateMarkerDTO.from(marker);
    }

//    /** 특정 카테고리의 마커개수 요청*/
    public Long getMarkerCountByRoomId(Long roomId) {
        return markerRepository.countByRoomId(roomId);
    }


    public List<CreateMarkerDTO> getMarkerListByCategoryId(String email, Long categoryId) {
        // 카테고리가 삭제되었으면 애초에 조회가 안됌
        RoomMember roomMember = getCategoryUserByEmailAndCategoryId(email, categoryId);

        /** 추방당함 또는 카테고리 를 나간경우 */
        if(!roomMember.getInviteState().equals(InviteState.INVITE)) {
            throw new PermissionException("권한이 없습니다");
        }

        List<Marker> markerList = markerRepository.getMarkerList(categoryId);
        return markerList.stream()
                .map(CreateMarkerDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CreateMarkerDTO updateMarker(String email, UpdateMarkerRequest req) {
        Marker marker = findMarkerById(req.getId());

        String creatorEmail = marker.getMember().getEmail();

        boolean isPermission = checkMarkerPermission(email, creatorEmail, marker.getRoom().getId());

        if(!isPermission) { throw new PermissionException("권한이 없습니다");}

        marker.update(req);

        return CreateMarkerDTO.from(marker);
    }

    @Transactional
    public Marker deleteMarker(String email, Long markerId) {
        Marker marker = findMarkerById(markerId);

        boolean isPermission = checkMarkerPermission(email,marker.getMember().getEmail(), marker.getRoom().getId());

        if(!isPermission) { throw new PermissionException("권한이 없습니다");}

        marker.delete();
        return marker;
    }

    private Marker findMarkerById(Long markerId) {
        return markerRepository.findByIdAndDeletedAtIsNull(markerId)
                .orElseThrow(() -> new InvalidRequestException("해당Id의 마커를 찾을수 없음"));
    }


    /** 권한 체크
     * 방장, 내가 생성한 마커, 나보다 권한이 낮은경우: true
     * 나머지 false
     * */
    private boolean checkMarkerPermission(String myEmail, String creatorEmail, Long categoryId) {
        RoomMember me = roomMemberRepository.getCategoryUserByEmailAndRoomId(myEmail, categoryId, RoomState.ACTIVE)
                .orElseThrow(() -> new InvalidRequestException("마커 생성자가 카테고리에 속해있지 않습니다."));

        RoomMember creator = roomMemberRepository.getCategoryUserByEmailAndRoomId(creatorEmail, categoryId, RoomState.ACTIVE)
                .orElseThrow(() -> new InvalidRequestException("해당 카테고리 소속이 아닙니다."));

        RoomMemberRole myPermission = me.getRoomMemberRole();

        // 내가 방장인 경우 필터링
        if(myPermission.equals(RoomMemberRole.OWNER)) {
            return true;
        }

        // 내가 생성자인 경우 필터링
        if(me.getMember().getEmail().equals(creator.getMember().getEmail())) {
            return true;
        }

        // 일반 유저, ReadOnly 필터링
        if(!myPermission.equals(RoomMemberRole.MANAGER)) {
            return false;
        }

        // 방장이 만든경우 필터링
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






    private RoomMember getCategoryUserByEmailAndCategoryId(String email, Long categoryId) {
        return roomMemberRepository.getCategoryUserByEmailAndRoomId(email, categoryId, RoomState.ACTIVE)
                .orElseThrow(() -> new InvalidRequestException("해당 카테고리유저를 찾을수 없습니다."));
    }
}
