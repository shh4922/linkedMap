package com.hyeonho.linkedmap.entity;

public enum CategoryState {
    ACTIVE("ACTIVE"), // 활성화

    DELETE("DELETE"); // 삭제된 카테고리

    final String categoryState;

    CategoryState(String categoryState) {
        this.categoryState = categoryState;
    }
}
