package com.hyeonho.linkedmap.data.request.room;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RoomJoinRequest {
    private UUID inviteKey;
    private Long roomId;
}
