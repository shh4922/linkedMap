package com.hyeonho.linkedmap.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

public enum CategoryUserRole {
    /**
     * 카테고리주인
     * 카테고리 삭제, 수정(all)
     * 마커 추가, 수정, 삭제
     * 초대링크 생성
     * 유저추방
     */
    ONWER,

    /**
     * 마커 추가, 수정, 삭제(all)
     * 마커 추가, 수정, 삭제
     * 초대링크 생성
     * 유저추방 - 자신과같은 등급orOnwer는 추방 불가
     */
    MANNAGER,

    /**
     * 마커 추가, 수정, 삭제(본인이 추가한 내용만)
     */
    USER,

    /**
     * 마커 읽기
     */
    READONLY

}

