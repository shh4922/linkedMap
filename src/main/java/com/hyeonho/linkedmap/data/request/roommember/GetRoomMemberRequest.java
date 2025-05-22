package com.hyeonho.linkedmap.data.request.roommember;

import lombok.Getter;

@Getter
public class GetRoomMemberRequest {
    private Long memberId;
    private Long roomId;
}
