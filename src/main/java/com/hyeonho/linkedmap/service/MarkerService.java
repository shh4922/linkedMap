package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.data.request.MarkerReq;
import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.entity.CategoryUser;
import com.hyeonho.linkedmap.entity.Marker;
import com.hyeonho.linkedmap.repository.CategoryRepository;
import com.hyeonho.linkedmap.repository.CategoryUserRepository;
import com.hyeonho.linkedmap.repository.MarkerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarkerService {

    private final MarkerRepository markerRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryUserRepository categoryUserRepository;

    public MarkerService(MarkerRepository markerRepository, CategoryRepository categoryRepository, CategoryUserRepository categoryUserRepository) {
        this.markerRepository = markerRepository;
        this.categoryRepository = categoryRepository;
        this.categoryUserRepository = categoryUserRepository;
    }


    /**
     * 1. 해당 카테고리 존재유무 확인
     * 2. 삭제된 카테고리인지 체크
     * 3. 해당 유저가 카테고리에 포함되어있는지 체크
     * 4. 마커 리스트 리턴
     * @param req
     * @return
     */
//    public List<Marker> getMarkerByCategoryId(MarkerReq req) {
//        Category category = categoryRepository.findById(req.getCategoryId())
//                .orElseThrow(()-> new IllegalArgumentException("카테고리가 존재하지 않습니다"));
//
//        if (category.getDeletedAt() != null) {
//            throw new IllegalStateException("삭제된 카테고리입니다.");
//        }
//
//        if(!categoryUserRepository.existsByCategoryIdAndMemberEmail(req.getCategoryId(), req.getUserEmail())) {
//            throw new IllegalStateException("해당 카테고리에 접근 불가능 합니다.");
//        }
//
//        return markerRepository.findMarkerByCategoryId(req.getCategoryId());
//    }
}
