package com.hyeonho.linkedmap.member.member;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter

public class MemberDeleteDto {
    private String email;
    private String username;
    private LocalDateTime deletedAt;

    @Builder
    public MemberDeleteDto(String email, String username, LocalDateTime deletedAt) {
        this.email = email;
        this.username = username;
        this.deletedAt = deletedAt;
    }
}
