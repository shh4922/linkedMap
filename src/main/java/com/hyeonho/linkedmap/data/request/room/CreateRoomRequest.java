package com.hyeonho.linkedmap.data.request.room;

import lombok.Getter;

@Getter
public class CreateRoomRequest {
    private String roomName;
    private String description;
    private String imageUrl;
}
