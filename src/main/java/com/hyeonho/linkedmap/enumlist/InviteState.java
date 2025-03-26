package com.hyeonho.linkedmap.enumlist;

/**
 * 수락된 상태
 */
public enum InviteState {
    PENDING("PENDING"), // 링크를 통해 누군가 들어오기 전 대기상태
    TIMEOUT("TIMEOUT"), // 초대 기한 만료
    INVITE("INVITE"), // 링크를 통해 초대 완료
    REJECT("REJECT"), // 거절??
    EXPELLED("EXPELLED"), // 방장에 의해 추방
    GETOUT("GETOUT"); // 나감



    final String inviteState;

    InviteState(String inviteState) {
        this.inviteState = inviteState;
    }
}
