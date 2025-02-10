package com.hyeonho.linkedmap.data.dto;

import com.hyeonho.linkedmap.entity.Marker;
import com.hyeonho.linkedmap.entity.Member;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MarkerRes {
    private Long id;
    private BigDecimal lat;
    private BigDecimal lng;
    private String title;
    private String description;
    private String creator;


    // 엔티티 → DTO 변환 메서드
    public static MarkerRes fromEntity(Marker marker) {
        return new MarkerRes(
                marker.getId(),
                marker.getLat(),
                marker.getLng(),
                marker.getTitle(),
                marker.getDescription(),
                marker.getMemberId().getUsername()
        );
    }
}
