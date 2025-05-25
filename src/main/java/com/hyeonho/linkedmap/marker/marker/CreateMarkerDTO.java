package com.hyeonho.linkedmap.marker.marker;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hyeonho.linkedmap.marker.Marker;
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
    private String creatorEmail;    // 만든사람 email
    private String creatorName;     // 만든사람 이름
    private String creatorRole;     // 만든사람 권한
    private Long roomId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Builder
    public CreateMarkerDTO(Long id, BigDecimal lat, BigDecimal lng, String title, String description, String address, String roadAddress, String storeType, String imageUrl, String creatorEmail, String creatorName, Long roomId, LocalDateTime createdAt, LocalDateTime updatedAt, String creatorRole) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.description = description;
        this.address = address;
        this.roadAddress = roadAddress;
        this.storeType = storeType;
        this.imageUrl = imageUrl;
        this.creatorEmail = creatorEmail;
        this.creatorName = creatorName;
        this.roomId = roomId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.creatorRole = creatorRole;
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

                .creatorEmail(marker.getMember().getEmail())
                .creatorName(marker.getMember().getUsername())
//                .creatorRole(marker.getMember())
                .roomId(marker.getRoom().getId())

                .createdAt(marker.getCreatedAt())
                .updatedAt(marker.getUpdatedAt())

                .build();
    }
}
