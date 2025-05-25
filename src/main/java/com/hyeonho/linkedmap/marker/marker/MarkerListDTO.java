package com.hyeonho.linkedmap.marker.marker;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MarkerListDTO {
    private Long markerId;
    private BigDecimal lat;
    private BigDecimal lng;
    private String title;
    private String description;
    private String storeType;
    private String address;
    private String roadAddress;
    private String imageUrl;

    private String creatorEmail;
    private String creatorName;
    private Long creatorId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
