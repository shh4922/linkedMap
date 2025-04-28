package com.hyeonho.linkedmap.data.dto.category;


import com.hyeonho.linkedmap.data.dto.member.RoomMemberDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RoomDetailDTO {
    private Long roomId;                    // 방의 Id
    private String roomName;            // 방 명
    private String roomDescription;     // 방 설명
    private String categoryOwner;           // 방 소유자
    private LocalDateTime createdAt;        // 방 생성일

    private List<RoomMemberDTO> member;    // 카테고리에 속한 유저
    private int markerCount;                // 카테고리에 있는 마커수

    @Builder
    public RoomDetailDTO(Long categoryId, String categoryName, String description, String owner, LocalDateTime createdAt, List<RoomMemberDTO> memberList, int markerCount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryDescription = description;
        this.categoryOwner = owner;
        this.createdAt = createdAt;
        this.member = memberList;
        this.markerCount = markerCount;
    }
}
