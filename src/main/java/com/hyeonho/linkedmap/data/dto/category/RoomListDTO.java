package com.hyeonho.linkedmap.data.dto.category;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hyeonho.linkedmap.entity.RoomMember;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class RoomListDTO {
    private Long id;
    private Long roomId;
    private String roomName;
    private String roomDescription;
    private String currentOwnerEmail;
    private String currentOwnerName;
    private String createUserEmail;
    private String createUserName;
    private String inviteState;
    private String myRole;
    private String roomState;

    // Setter 추가
    @Setter
    private Integer markerCount;
    @Setter
    private Integer memberCount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime inviteDate;


    @Builder
    public RoomListDTO(
            Long id,
            Long roomId,
            String roomName,
            String roomDescription,
            String currentOwnerName,
            String currentOwnerEmail,
            String createUserEmail,
            String createUserName,
            String inviteState,
            LocalDateTime inviteDate,
            String myRole,
            String roomState)
    {
        this.id = id;
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomDescription = roomDescription;
        this.currentOwnerEmail = currentOwnerEmail;
        this.currentOwnerName = currentOwnerName;
        this.createUserEmail = createUserEmail;
        this.createUserName = createUserName;
        this.myRole = myRole;
        this.inviteState = inviteState;

        this.roomState = roomState;
        this.inviteDate = inviteDate;
    }

    public static RoomListDTO fromEntity(RoomMember roomMember) {
        return RoomListDTO.builder()
                .id(roomMember.getId())
                .roomId(roomMember.getRoom().getId())
                .roomName(roomMember.getRoom().getName())
                .roomDescription(roomMember.getRoom().getDescription())
                .currentOwnerEmail(roomMember.getRoom().getCurrentOwner().getEmail())
                .currentOwnerName(roomMember.getRoom().getCurrentOwner().getUsername())
                .createUserEmail(roomMember.getRoom().getCreator().getEmail())
                .createUserName(roomMember.getRoom().getCreator().getUsername())
                .myRole(roomMember.getRoomMemberRole().name())
                .inviteState(roomMember.getInviteState().name())
                .roomState(roomMember.getRoom().getRoomState().name())
                .inviteDate(roomMember.getCreatedAt())
                .build();
    }
}
