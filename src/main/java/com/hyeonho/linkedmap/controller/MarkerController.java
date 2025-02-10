package com.hyeonho.linkedmap.controller;

import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.data.dto.MarkerRes;
import com.hyeonho.linkedmap.data.request.DeleteMarkerReq;
import com.hyeonho.linkedmap.data.request.MarkerReq;
import com.hyeonho.linkedmap.entity.Marker;
import com.hyeonho.linkedmap.service.MarkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/v1")
public class MarkerController {

    private final MarkerService markerService;

    public MarkerController(MarkerService markerService) {
        this.markerService = markerService;
    }

    /**
     * 유저정보 & 카테고리id 받아서 마커 리턴
     * @param req
     * @return
     */
//    @GetMapping("/markers")
//    public ResponseEntity<DefaultResponse<List<MarkerRes>>> getMarkersByCategoryId(MarkerReq req) {
//        List<Marker> markers = markerService.getMarkerByCategoryId(req);
//        List<MarkerRes> markerResList = markers.stream().map(MarkerRes::fromEntity).toList();
//        return ResponseEntity.ok(DefaultResponse.success(markerResList));
//    }

//    @PostMapping("/delete/marker")
//    public List<MarkerRes> deleteMarker(DeleteMarkerReq req) {
//        List<Marker> markers = markerService.
//        return markers.stream().map(MarkerRes::fromEntity).toList();
//    }
}
