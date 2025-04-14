package com.hyeonho.linkedmap.data.request.marker;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UpdateMarkerRequest {
    private Long id;
    private BigDecimal lat;
    private BigDecimal lng;
    private String title;
    private String description;
    private String storeType;
    private String address;
    private String roadAddress;
    private String imageUrl;
}
