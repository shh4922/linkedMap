package com.hyeonho.linkedmap.room.controller;

import com.hyeonho.linkedmap.auth.JWTProvider;
import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.room.data.RoomDetailDTO;
import com.hyeonho.linkedmap.room.data.RoomListDTO;
import com.hyeonho.linkedmap.data.request.room.RoomUpdateRequest;
import com.hyeonho.linkedmap.data.request.room.CreateRoomRequest;
import com.hyeonho.linkedmap.room.entity.Room;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.roommember.RoomMemberRepository;
import com.hyeonho.linkedmap.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final JWTProvider jwtProvider;
    private final RoomMemberRepository roomMemberRepository;


    /** 방 생성 */
    @PostMapping("/room/create")
    public ResponseEntity<DefaultResponse<String>> createRoom(@AuthenticationPrincipal Long memberId,
                                                              @RequestPart("image") Optional<MultipartFile> file,
                                                              @RequestPart("dto") CreateRoomRequest request) {
        if(request.getRoomName().isEmpty()) {
            return ResponseEntity.ok(DefaultResponse.error(400,"필수항목이 비어있습니다."));
        }

        Room room = roomService.createRoom(memberId,file, request);
        return ResponseEntity.ok(DefaultResponse.success("1"));
    }

    /**
     * 내가 속해있는 방 조회
     * InviteState 가 Invited 인 것만 조회해야함.
     * 하지만 roomState 가 delete 면 화면에서 삭제된방 이라고 보여줘야함.
     */
    @GetMapping("/room/me")
    public ResponseEntity<DefaultResponse<List<RoomListDTO>>> getMyRooms(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(DefaultResponse.success(roomService.getMyRooms(memberId)));
    }



    /**
     * 특정 유저가 속한 방 조회
     * 초대되었는데, 방이 삭제되어서 비활성화된거면 유저페이지에선 안보이게 해야함. 내꺼에선 보여야함. 왜냐면 나가기버튼 활성화 시켜야함
     * InviteState = invite (초대된것만 보이게)
     * RoomState = Active (활성화된 방만 보이게)
     */
    @GetMapping("/room/include/{memberId}")
    public ResponseEntity<DefaultResponse<List<RoomListDTO>>> getRoomListById(@PathVariable(value = "memberId") Long memberId) {
        if(memberId == null) { throw new InvalidRequestException("회원정보가 없습니다."); }


        return ResponseEntity.ok(DefaultResponse.success(roomService.getRoomListById(memberId)));
    }


    /**
     * 특정 방의 디테일 정보
     * 방 정보, 유저리스트 등등...
     * @param roomId
     * @return
     */
    @GetMapping("/room/detail/{roomId}")
    public ResponseEntity<DefaultResponse<RoomDetailDTO>> getRoomDetail(@AuthenticationPrincipal Long memberId, @PathVariable("roomId") Long roomId) {
        if(roomId == null) { throw new InvalidRequestException("해당 방의 정보가 없습니다"); }

        return ResponseEntity.ok(DefaultResponse.success(roomService.getRoomDetail(memberId, roomId)));
    }


    /**
     * 방 삭제 (방의 방장이 해당 방을 삭제함)
     * 방 삭제시, RoomMember 있는 곳에 deleted_at 시간 또한 업데이트 해주어야함.
     * RoomMember 가 있는 해당 Room의 roomState 를 Delete로 변경
     * @return
     */
    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<DefaultResponse<String>> deleteRoom(@AuthenticationPrincipal Long memberId, @PathVariable("roomId") Long roomId) {
        if(roomId == null) { throw new InvalidRequestException("방 아이디값 없음"); }

        Room room = roomService.deleteRoomByRoomId(memberId, roomId);

        if(room.getDeletedAt() == null) {
            return ResponseEntity.ok(DefaultResponse.error(400,"삭제실패"));
        }
        return ResponseEntity.ok(DefaultResponse.success("0"));
    }

    /**
     * 방 나가기.
     */
    @PostMapping("/room/getout/{roomId}")
    public ResponseEntity<DefaultResponse<String>> getOutRoom(@AuthenticationPrincipal Long memberId, @PathVariable("roomId") Long roomId) {
        if(roomId == null) { throw new InvalidRequestException("방 아이디값 없음");}

        int result = roomService.getOutRoomByMemberIdAndRoomId(List.of(memberId),roomId);
        if(result == 0) {
            return ResponseEntity.ok(DefaultResponse.error(400,"실패"));
        }
        return ResponseEntity.ok(DefaultResponse.success("0"));
    }


    /**
     * 방 업데이트
     * 방장인지 체크후 업데이트
     * @param req
     * @return
     */
    @PutMapping("/room/update")
    public ResponseEntity<DefaultResponse<String>> updateRoom(@AuthenticationPrincipal Long memberId,
                                                              @RequestPart("image") Optional<MultipartFile> file,
                                                              @RequestPart("dto") RoomUpdateRequest request) {
        return ResponseEntity.ok(DefaultResponse.success(roomService.updateRoom(memberId, file, request)));
    }


}
