package com.hyeonho.linkedmap.data.request;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Optional;

@Getter
public class CreateMarkerRequest {
    private String title;
    private BigDecimal lat;
    private BigDecimal lng;
    private String description;  // Optional로 null을 처리

    private String storeType;
    private String address;
    private String roadAddress;
    private Long roomId;  // 카테고리 ID
}
