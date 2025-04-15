package com.hyeonho.linkedmap.data.dto.marker;

import com.hyeonho.linkedmap.entity.Marker;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private String creator; // 작성자 email
    private Long categoryId;
    private LocalDateTime createdAt;

    @Builder
    public CreateMarkerDTO(Long id, BigDecimal lat, BigDecimal lng, String title,
                          String description, String address, String roadAddress,
                          String storeType, String imageUrl, String creator, LocalDateTime createdAt, Long categoryId) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.description = description;
        this.address = address;
        this.roadAddress = roadAddress;
        this.storeType = storeType;
        this.imageUrl = imageUrl;
        this.creator = creator;
        this.createdAt = createdAt;
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
                .creator(marker.getMember().getEmail())
                .createdAt(marker.getCreatedAt())
                .categoryId(marker.getCategory().getId())
                .build();
    }
}
