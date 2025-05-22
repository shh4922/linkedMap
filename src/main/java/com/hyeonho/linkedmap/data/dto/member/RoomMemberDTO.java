package com.hyeonho.linkedmap.data.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RoomMemberDTO {
    private Long memberId;
    private String email;
    private String name;
    private String role;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime inviteDate;

    @Builder
    public RoomMemberDTO(Long memberId, String email, String name, String role, LocalDateTime inviteDate) {
        this.memberId = memberId;
        this.email = email;
        this.name = name;
        this.role = role;
        this.inviteDate = inviteDate;
    }

}
