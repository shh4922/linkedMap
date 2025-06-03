package com.hyeonho.linkedmap.data.request.marker;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UpdateMarkerRequest {
    private Long markerId;

    private String title;
    private String description;
    private String storeType;

}
