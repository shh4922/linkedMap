package com.hyeonho.linkedmap.data.request;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Optional;

@Getter
public class CreateMarkerRequest {
    private BigDecimal lat;
    private BigDecimal lng;
    private String title;
    private String description;  // Optional로 null을 처리
    private String storeType;
    private String address;
    private String roadAddress;
    private Long categoryId;  // 카테고리 ID
    private String imageUrl;  // Optional로 이미지 URL 처리
}
