package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.data.dto.marker.CreateMarkerDTO;
import com.hyeonho.linkedmap.data.request.CreateMarkerRequest;
import com.hyeonho.linkedmap.data.request.MarkerReq;
import com.hyeonho.linkedmap.data.request.marker.UpdateMarkerRequest;
import com.hyeonho.linkedmap.entity.*;
import com.hyeonho.linkedmap.enumlist.CategoryUserRole;
import com.hyeonho.linkedmap.enumlist.InviteState;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.error.PermissionException;
import com.hyeonho.linkedmap.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarkerService {

    private final MarkerRepository markerRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryUserRepository categoryUserRepository;
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
       CategoryUser categoryUser = getCategoryUserByEmailAndCategoryId(email, req.getCategoryId());

       /** 추방당함 또는 카테고리 를 나간경우 */
       if(!categoryUser.getInviteState().equals(InviteState.INVITE)) {
           throw new PermissionException("권한이 없습니다");
       }

       /** 권한이 없는경우 */
       if(categoryUser.getCategoryUserRole().equals(CategoryUserRole.READ_ONLY)) { //403
           throw new PermissionException("권한이 없습니다");
       }

       Category category = categoryRepository.findById(req.getCategoryId()) // 403
               .orElseThrow(() -> new InvalidRequestException("해당 카테고리가 존재하지않습니다"));

       Member member = memberRepository.findById(email)
               .orElseThrow(() -> new InvalidRequestException("유저없음"));

       Marker marker = Marker.builder()
               .request(req)
               .member(member)
               .category(category)
               .build();

       markerRepository.save(marker);
       return CreateMarkerDTO.from(marker);
    }


    public List<CreateMarkerDTO> getMarkerListByCategoryId(String email, Long categoryId) {
        // 카테고리가 삭제되었으면 애초에 조회가 안됌
        CategoryUser categoryUser = getCategoryUserByEmailAndCategoryId(email, categoryId);

        /** 추방당함 또는 카테고리 를 나간경우 */
        if(!categoryUser.getInviteState().equals(InviteState.INVITE)) {
            throw new PermissionException("권한이 없습니다");
        }

        List<Marker> markerList = markerRepository.getMarkerList(categoryId);
        return markerList.stream()
                .map(CreateMarkerDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CreateMarkerDTO updateMarker(String email, UpdateMarkerRequest req) {
        Marker marker = markerRepository.findById(req.getId())
                .orElseThrow(() -> new InvalidRequestException("해당Id의 마커를 찾을수 없음"));

        CategoryUser creator = categoryUserRepository.getCategoryUserByEmailAndCategoryId(marker.getMember().getEmail(), marker.getCategory().getId(), CategoryState.ACTIVE)
                .orElseThrow(() -> new InvalidRequestException("마커 생성자가 카테고리에 속해있지 않습니다."));


        if(email.equals(marker.getMember().getEmail())) { // 내가 만든 경우
            marker.update(req);
        }
        else { // 내가 만든게 아닌경우
            CategoryUser me = categoryUserRepository.getCategoryUserByEmailAndCategoryId(email, marker.getCategory().getId(), CategoryState.ACTIVE)
                    .orElseThrow(() -> new InvalidRequestException("해당 카테고리 소속이 아닙니다."));

            CategoryUserRole myRole = me.getCategoryUserRole();
            CategoryUserRole creatorRole = creator.getCategoryUserRole();

            // 업데이트 가능 조건
            if(myRole.equals(CategoryUserRole.OWNER)) { // 내가 방장인 경우
                marker.update(req);
            } else if ( myRole.equals(CategoryUserRole.MANAGER) && // 내가 매니저고 만든애가 나보다 권한이 낮은경우
                    (creatorRole.equals(CategoryUserRole.USER) ||creatorRole.equals(CategoryUserRole.READ_ONLY))
            ) {
                marker.update(req);
            } else {
                throw new PermissionException("마커를 수정할 권한이 없습니다.");
            }
        }
        return CreateMarkerDTO.from(marker);
    }




    private CategoryUser getCategoryUserByEmailAndCategoryId(String email, Long categoryId) {
        return categoryUserRepository.getCategoryUserByEmailAndCategoryId(email, categoryId, CategoryState.ACTIVE)
                .orElseThrow(() -> new InvalidRequestException("해당 카테고리유저를 찾을수 없습니다."));
    }
}
