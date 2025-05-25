package com.hyeonho.linkedmap.room;

public enum RoomState {
    ACTIVE("ACTIVE"), // 활성화


    /**
     * CategoryState 가 Delete 이면 화면에선 삭제된 카테고리 라고 표시.
     * 나가기 를 권유.
     */
    DELETE("DELETE");



    final String categoryState;

    RoomState(String categoryState) {
        this.categoryState = categoryState;
    }
}
