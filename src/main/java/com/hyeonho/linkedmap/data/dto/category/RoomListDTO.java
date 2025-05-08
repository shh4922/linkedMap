package com.hyeonho.linkedmap.data.dto.category;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hyeonho.linkedmap.entity.RoomMember;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 특정 유저가 속한 카테고리 리스트와, 내가속한 카테고리 리스트의 응답이 거의 유사
 * 있을수도 있고 없을수도 있는거는 ? 로 처리 하도록 수정
 */
// TODO: ? 로 있을수도 있고 없을수도 있게 수정

@Getter
public class RoomListDTO {
    private Long id;
    private Long roomId;
    private String roomName;
    private String roomDescription;

    private Long currentOwnerId;
    private String currentOwnerEmail;
    private String currentOwnerName;
    private String createUserEmail;
    private String createUserName;

    private String inviteState; // 초대상태 (초대됨, 나감, 강퇴)
    private String roomState;   // 방 상태 (삭제됨, 활성화됨)
    private String role;      // 내 권한

    // Setter 추가
    @Setter
    private Integer markerCount;    // 마커 개수
    @Setter
    private Integer memberCount;    // 초대된 유저 수

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime roomCreateAt;   // 방 생성일

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime inviteDate;   // 초대된 날짜


    @Builder
    public RoomListDTO(
            Long id,
            Long roomId,
            String roomName,
            String roomDescription,
            Long currentOwnerId,
            String currentOwnerName,
            String currentOwnerEmail,
            String createUserEmail,
            String createUserName,
            String inviteState,
            LocalDateTime roomCreateAt,
            LocalDateTime inviteDate,
            String role,
            String roomState)
    {
        this.id = id;
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomDescription = roomDescription;
        this.currentOwnerId = currentOwnerId;
        this.currentOwnerEmail = currentOwnerEmail;
        this.currentOwnerName = currentOwnerName;
        this.createUserEmail = createUserEmail;
        this.createUserName = createUserName;
        this.role = role;
        this.roomCreateAt = roomCreateAt;
        this.inviteState = inviteState;

        this.roomState = roomState;
        this.inviteDate = inviteDate;
    }

    public static RoomListDTO fromEntityMyRoom(RoomMember roomMember) {
        return RoomListDTO.builder()
                .id(roomMember.getId())
                .roomId(roomMember.getRoom().getId())
                .roomName(roomMember.getRoom().getName())
                .roomDescription(roomMember.getRoom().getDescription())
                .currentOwnerId(roomMember.getRoom().getCurrentOwner().getId())
                .currentOwnerEmail(roomMember.getRoom().getCurrentOwner().getEmail())
                .currentOwnerName(roomMember.getRoom().getCurrentOwner().getUsername())
                .createUserName(roomMember.getRoom().getCreator().getUsername())
                .createUserEmail(roomMember.getRoom().getCreator().getEmail())
                .role(roomMember.getRoomMemberRole().name())
                .roomCreateAt(roomMember.getRoom().getCreatedAt())

                .inviteState(roomMember.getInviteState().name())
                .inviteDate(roomMember.getInviteAt())
                .build();
    }

    public static RoomListDTO fromEntityByEmail(RoomMember roomMember) {
        return RoomListDTO.builder()
                .roomId(roomMember.getRoom().getId())
                .roomName(roomMember.getRoom().getName())
                .roomDescription(roomMember.getRoom().getDescription())
                .currentOwnerEmail(roomMember.getRoom().getCurrentOwner().getEmail())
                .currentOwnerName(roomMember.getRoom().getCurrentOwner().getUsername())
                .createUserEmail(roomMember.getRoom().getCreator().getEmail())
                .createUserName(roomMember.getRoom().getCreator().getUsername())
                .roomCreateAt(roomMember.getRoom().getCreatedAt())

                .role(roomMember.getRoomMemberRole().name())
                .inviteDate(roomMember.getInviteAt())

                .build();
    }
}
