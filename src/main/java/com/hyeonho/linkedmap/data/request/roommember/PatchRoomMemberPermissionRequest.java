package com.hyeonho.linkedmap.data.request.roommember;

import lombok.Getter;

@Getter
public class PatchRoomMemberPermissionRequest {
    private Long targetMemberId;
    private Long roomId;
    private String permission;

}
