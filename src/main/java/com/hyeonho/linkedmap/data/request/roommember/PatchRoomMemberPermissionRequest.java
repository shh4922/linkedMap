package com.hyeonho.linkedmap.data.request.roommember;

import lombok.Getter;

@Getter
public class PatchRoomMemberPermissionRequest {
    private Long roomMemberId;
    private String permission;
    private Long roomId;

}
