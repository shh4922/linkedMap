package com.hyeonho.linkedmap.enumlist;

public enum InviteState {
    PENDING("PENDING"), // 대기
    TIMEOUT("TIMEOUT"), // 기한만료
    INVITE("INVITE"), // 수락
    REJECT("REJECT"), // 거절
    EXPELLED("EXPELLED"), // 추방
    GETOUT("GETOUT"), // 나감
    WITHDRAW("WITHDRAW"); // 회원탈퇴

    String inviteState;

    InviteState(String inviteState) {
        this.inviteState = inviteState;
    }
}
