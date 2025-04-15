package com.hyeonho.linkedmap.controller;

import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.data.dto.MarkerRes;
import com.hyeonho.linkedmap.data.dto.marker.CreateMarkerDTO;
import com.hyeonho.linkedmap.data.request.CategoryUpdateReq;
import com.hyeonho.linkedmap.data.request.CreateMarkerRequest;
import com.hyeonho.linkedmap.data.request.DeleteMarkerReq;
import com.hyeonho.linkedmap.data.request.MarkerReq;
import com.hyeonho.linkedmap.data.request.marker.UpdateMarkerRequest;
import com.hyeonho.linkedmap.entity.CategoryUser;
import com.hyeonho.linkedmap.entity.Marker;
import com.hyeonho.linkedmap.service.MarkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MarkerController {

    private final MarkerService markerService;


    /**
     * 유저정보 & 카테고리id 받아서 마커 리턴
     * @param req
     * @return
     */
    @PostMapping("/marker/create")
    public ResponseEntity<DefaultResponse<CreateMarkerDTO>> createMarker(@AuthenticationPrincipal String email, @RequestBody CreateMarkerRequest req) {
        CreateMarkerDTO markerDTO = markerService.createMarker(req,email);
        return ResponseEntity.ok(DefaultResponse.success(markerDTO));
    }

    @GetMapping("/markers/{categoryId}")
    public ResponseEntity<DefaultResponse<List<CreateMarkerDTO>>> getMarkerListByCategoryId(@AuthenticationPrincipal String email, @PathVariable(value = "categoryId") Long categoryId) {
        List<CreateMarkerDTO> markerDTOS = markerService.getMarkerListByCategoryId(email, categoryId);
        return ResponseEntity.ok(DefaultResponse.success(markerDTOS));
    }

    @PutMapping("/marker/update")
    public ResponseEntity<DefaultResponse<CreateMarkerDTO>> updateMarker(@AuthenticationPrincipal String email, @RequestBody UpdateMarkerRequest req) {
        CreateMarkerDTO markerDTO = markerService.updateMarker(email, req);
        return ResponseEntity.ok(DefaultResponse.success(markerDTO));
    }

    @DeleteMapping("/marker/{markerId}")
    public ResponseEntity<DefaultResponse<Marker>> deleteMarker(@AuthenticationPrincipal String email, @PathVariable(value = "markerId") Long markerId) {
        Marker marker = markerService.deleteMarker(email,markerId);
        return ResponseEntity.ok(DefaultResponse.success(marker));
    }
}
