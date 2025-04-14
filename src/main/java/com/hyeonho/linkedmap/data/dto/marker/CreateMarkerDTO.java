package com.hyeonho.linkedmap.data.dto.marker;

import com.hyeonho.linkedmap.entity.Marker;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CreateMarkerDTO {
    private Long id;
    private BigDecimal lat;
    private BigDecimal lng;
    private String title;
    private String description;
    private String address;
    private String roadAddress;
    private String storeType;
    private String imageUrl;
    private String createdBy; // 작성자 email
    private Long categoryId;

    @Builder
    public CreateMarkerDTO(Long id, BigDecimal lat, BigDecimal lng, String title,
                          String description, String address, String roadAddress,
                          String storeType, String imageUrl, String createdBy, Long categoryId) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.description = description;
        this.address = address;
        this.roadAddress = roadAddress;
        this.storeType = storeType;
        this.imageUrl = imageUrl;
        this.createdBy = createdBy;
        this.categoryId = categoryId;
    }

    public static CreateMarkerDTO from(Marker marker) {
        return CreateMarkerDTO.builder()
                .id(marker.getId())
                .lat(marker.getLat())
                .lng(marker.getLng())
                .title(marker.getTitle())
                .description(marker.getDescription())
                .address(marker.getAddress())
                .roadAddress(marker.getRoadAddress())
                .storeType(marker.getStoreType())
                .imageUrl(marker.getImageUrl())
                .createdBy(marker.getMember().getEmail())
                .categoryId(marker.getCategory().getId())
                .build();
    }
}
