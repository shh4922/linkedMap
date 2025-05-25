package com.hyeonho.linkedmap.roommember;

import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.data.request.roommember.GetRoomMemberRequest;
import com.hyeonho.linkedmap.data.request.roommember.PatchRoomMemberPermissionRequest;
import com.hyeonho.linkedmap.data.request.roommember.PostExpelledRoomMember;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * roomMember 의 권한을 변경
     * @param memberId
     * @param request
     * @return
     */
    @PatchMapping("/roommember/update/permission")
    public ResponseEntity<DefaultResponse<String>> updateRoomMemberPermission(@AuthenticationPrincipal Long memberId, @RequestBody PatchRoomMemberPermissionRequest request) {
        if(request.getPermission() == null || request.getPermission().isEmpty() || request.getRoomMemberId() == null || request.getRoomId() == null) {
            throw new InvalidRequestException("데이터가 비어있습니다.");
        }
        int result = roomMemberService.updateRoomMemberPermission(memberId, request);
        if(result == 1) {
            return ResponseEntity.ok(DefaultResponse.success("0"));
        }
        return ResponseEntity.ok(DefaultResponse.error(400,"권한 업데이트 실패"));
    }

    @PostMapping("/roommember/expelled")
    public ResponseEntity<DefaultResponse<String>> expelledRoomMember(@AuthenticationPrincipal Long memberId, @RequestBody PostExpelledRoomMember request) {
        if(request.getRoomMemberId() == null || request.getRoomId() == null) {
            throw new InvalidRequestException("roomMember 정보가 없습니다.");
        }
        return ResponseEntity.ok(DefaultResponse.success(roomMemberService.expelledRoomMember(memberId, request)));
    }
}
