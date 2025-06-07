package com.hyeonho.linkedmap.room.data;

import com.hyeonho.linkedmap.member.member.RoomMemberDTO;
import com.hyeonho.linkedmap.roommember.RoomMemberRole;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RoomDetailDTO {
    private Long roomId;                    // 방의 Id
    private String roomName;                // 방 명
    private String roomDescription;         // 방 설명
    private RoomMemberRole myRole;                  // 내 권한 (방장, 일반유저 등)
    private Long currentRoomOwnerId;        // 방장 Id
    private String currentRoomOwnerName;    // 방장 이름
    private String currentRoomOwnerEmail;   // 방장 이메일
    private String createUserEmail;         // 생성자 이메일
    private String createUserName;          // 생성자 이름
    private LocalDateTime createdAt;        // 방 생성일
    private String imageUrl;                // 커버 이미지
    private Long markerCount;                // 방의 있는 마커수

    private List<RoomMemberDTO> memberList;

    public RoomDetailDTO(
            Long roomId,
            String roomName,
            String roomDescription,
            String imageUrl,
            RoomMemberRole myRole,

            Long currentRoomOwnerId,
            String currentRoomOwnerName,
            String currentRoomOwnerEmail,
            String createUserEmail,
            String createUserName,

            LocalDateTime createdAt,

            Long markerCount
    ) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomDescription = roomDescription;
        this.myRole = myRole;
        this.currentRoomOwnerId = currentRoomOwnerId;
        this.currentRoomOwnerName = currentRoomOwnerName;
        this.currentRoomOwnerEmail = currentRoomOwnerEmail;
        this.createUserEmail = createUserEmail;
        this.createUserName = createUserName;
        this.createdAt = createdAt;
        this.imageUrl = imageUrl;
        this.markerCount = markerCount;
    }

    public void setMembers(List<RoomMemberDTO> memberList) {
        this.memberList = memberList;
    }
}
