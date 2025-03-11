package com.hyeonho.linkedmap.entity;

public enum CategoryState {
    ACTIVE("ACTIVE"), // 활성화

    DELETE("DELETE"); // 유저가 deletePending상태의 카테고리를 보면, 삭제된카테고리입니다. 가 나오고, 삭제를 누를시, delete로 변경

    final String categoryState;

    CategoryState(String categoryState) {
        this.categoryState = categoryState;
    }
}
