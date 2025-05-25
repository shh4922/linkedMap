package com.hyeonho.linkedmap.marker;

import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.marker.marker.CreateMarkerDTO;
import com.hyeonho.linkedmap.data.request.CreateMarkerRequest;
import com.hyeonho.linkedmap.data.request.marker.UpdateMarkerRequest;
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
    public ResponseEntity<DefaultResponse<CreateMarkerDTO>> createMarker(@AuthenticationPrincipal Long memberId, @RequestBody CreateMarkerRequest req) {
        CreateMarkerDTO markerDTO = markerService.createMarker(req,memberId);
        return ResponseEntity.ok(DefaultResponse.success(markerDTO));
    }

    @GetMapping("/markers/{roomId}")
    public ResponseEntity<DefaultResponse<List<CreateMarkerDTO>>> getMarkerListByCategoryId(@AuthenticationPrincipal Long memberId, @PathVariable(value = "roomId") Long roomId) {
        List<CreateMarkerDTO> markerDTOS = markerService.getMarkerListByRoomId(memberId, roomId);
        return ResponseEntity.ok(DefaultResponse.success(markerDTOS));
    }

    @PutMapping("/marker/update")
    public ResponseEntity<DefaultResponse<CreateMarkerDTO>> updateMarker(@AuthenticationPrincipal Long memberId, @RequestBody UpdateMarkerRequest req) {
        CreateMarkerDTO markerDTO = markerService.updateMarker(memberId, req);
        return ResponseEntity.ok(DefaultResponse.success(markerDTO));
    }

    @DeleteMapping("/marker/{markerId}")
    public ResponseEntity<DefaultResponse<Marker>> deleteMarker(@AuthenticationPrincipal Long memberId, @PathVariable(value = "markerId") Long markerId) {
        Marker marker = markerService.deleteMarker(memberId,markerId);
        return ResponseEntity.ok(DefaultResponse.success(marker));
    }
}
