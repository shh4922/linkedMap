package com.hyeonho.linkedmap.data.request.room;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomUpdateRequest {
    Long roomId;
    String roomName;
    String description;
}
