package com.hyeonho.linkedmap.data.dto.category;


import com.hyeonho.linkedmap.data.dto.member.RoomMemberDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

// TODO: CurrentRoomOwnerId 추가해야함.

@Getter
public class RoomDetailDTO {
    private Long roomId;                    // 방의 Id
    private String roomName;                // 방 명
    private String roomDescription;         // 방 설명
    private Long currentRoomOwnerId;        // 방장 Id
    private String currentRoomOwnerName;    // 방장 이름
    private String currentRoomOwnerEmail;   // 방장 이메일
    private String createUserEmail;         // 생성자 이메일
    private String createUserName;          // 생성자 이름
    private LocalDateTime createdAt;        // 방 생성일
    private String imageUrl;                // 커버 이미지

    private List<RoomMemberDTO> memberList;    // 카테고리에 속한 유저
    private int markerCount;                // 카테고리에 있는 마커수

    @Builder
    public RoomDetailDTO(
            Long roomId,
            String roomName,
            String roomDescription,
            Long currentRoomOwnerId,
            String currentRoomOwnerName,
            String currentRoomOwnerEmail,
            String createUserEmail,
            String createUserName,
            LocalDateTime createdAt,
            String imageUrl,
            List<RoomMemberDTO> memberList,
            int markerCount
    ) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomDescription = roomDescription;
        this.currentRoomOwnerId = currentRoomOwnerId;
        this.currentRoomOwnerName = currentRoomOwnerName;
        this.currentRoomOwnerEmail = currentRoomOwnerEmail;
        this.createUserEmail = createUserEmail;
        this.createUserName = createUserName;
        this.createdAt = createdAt;
        this.imageUrl = imageUrl;
        this.memberList = memberList;
        this.markerCount = markerCount;
    }
}
