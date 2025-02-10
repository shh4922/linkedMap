package com.hyeonho.linkedmap.enumlist;

public enum CategoryUserRole {
    OWNER("OWNER"), /**말해뭐해 모든권한 다 있음 ㅋㅋ*/

    MANAGER("MANAGER"),  /** 마커 추가, 수정, 삭제(all), 마커 추가, 수정, 삭제, 초대링크 생성, 유저추방(자신과같은 등급orOnwer는 추방 불가)*/

    USER("USER"), /** 마커 추가, 수정, 삭제(본인이 추가한 내용만) */

    READ_ONLY("READ_ONLY");    /** 마커 읽기 */

    String categoryUserRole;

    CategoryUserRole(String categoryUserRole) {
        this.categoryUserRole = categoryUserRole;
    }

}

