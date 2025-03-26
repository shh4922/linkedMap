package com.hyeonho.linkedmap.entity;

public enum CategoryState {
    ACTIVE("ACTIVE"), // 활성화

    DELETE("DELETE"); // inviteState 가 Active인데, CategoryState 가 Delete이면 화면에선 삭제된카테고리 라고 표시하고 , 나가기를 권유.



    final String categoryState;

    CategoryState(String categoryState) {
        this.categoryState = categoryState;
    }
}
