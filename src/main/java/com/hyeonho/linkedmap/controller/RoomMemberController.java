package com.hyeonho.linkedmap.controller;

import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.data.request.roommember.GetRoomMemberRequest;
import com.hyeonho.linkedmap.data.request.roommember.PatchRoomMemberPermissionRequest;
import com.hyeonho.linkedmap.entity.RoomMember;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.service.RoomMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RoomMemberController {

    private final RoomMemberService roomMemberService;

    @GetMapping("/roommember")
    public ResponseEntity<DefaultResponse<RoomMember>> getRoomMember(GetRoomMemberRequest request) {
        if(request.getMemberId() == null || request.getRoomId() == null) {
            throw new InvalidRequestException("파라미터가 비어있습니다.");
        }

        RoomMember roomMember = roomMemberService.getRoomMember(request);
        return ResponseEntity.ok(DefaultResponse.success(roomMember));
    }

    @PatchMapping("/roommember/update/permission")
    public ResponseEntity<DefaultResponse<String>> updateRoomMemberPermission(@AuthenticationPrincipal Long memberId, @RequestBody PatchRoomMemberPermissionRequest request) {
        if(request.getPermission() == null || request.getPermission().isEmpty() || request.getRoomId() == null || request.getTargetMemberId() == null) {
            throw new InvalidRequestException("데이터가 비어있습니다.");
        }
        int result = roomMemberService.updateRoomMemberPermission(memberId, request);
        if(result == 1) {
            return ResponseEntity.ok(DefaultResponse.success("0"));
        }
        return ResponseEntity.ok(DefaultResponse.error(400,"권한 업데이트 실패"));
    }
}
