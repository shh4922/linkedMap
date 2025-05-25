package com.hyeonho.linkedmap.invite;

/**
 * 수락된 상태
 */
public enum InviteState {
    PENDING("PENDING"), // 링크를 통해 누군가 들어오기 전 대기상태
    TIMEOUT("TIMEOUT"), // 초대 기한 만료

    /** 링크를 통해 초대 완료. 실제로 보여지는건 Invite 된 카테고리만 보여짐. */
    INVITE("INVITE"),
    REJECT("REJECT"), // 거절??
    EXPELLED("EXPELLED"), // 방장에 의해 추방

    /** 카테고리를 나가면 더이상 볼수없음. 본인이 삭제시 카테고리상태는 delete로 바꾸고 본인은 getout 으로 바꿈*/
    GETOUT("GETOUT");



    final String inviteState;

    InviteState(String inviteState) {
        this.inviteState = inviteState;
    }
}
