package com.hyeonho.linkedmap.data.request.room;

import lombok.Getter;

@Getter
public class CreateRoomRequest {
    private String roomName;
    private String description;
    private String imageUrl;
    private String contentType; // 이미지의 MIME 타입 (예: image/png, image/jpeg 등)
}
