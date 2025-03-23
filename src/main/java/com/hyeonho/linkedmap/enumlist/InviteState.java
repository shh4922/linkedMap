package com.hyeonho.linkedmap.enumlist;

/**
 * 수락된 상태
 */
public enum InviteState {
    PENDING("PENDING"), // 대기
    TIMEOUT("TIMEOUT"), // 초대 기한 만료
    INVITE("INVITE"), // 수락
    REJECT("REJECT"), // 거절
    EXPELLED("EXPELLED"), // 추방
    GETOUT("GETOUT"); // 나감



    final String inviteState;

    InviteState(String inviteState) {
        this.inviteState = inviteState;
    }
}
