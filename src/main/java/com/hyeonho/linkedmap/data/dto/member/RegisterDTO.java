package com.hyeonho.linkedmap.data.dto.member;

import com.hyeonho.linkedmap.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RegisterDTO {
    private String email;
    private String username;
    private LocalDateTime createdAt;

    @Builder
    public RegisterDTO(Member member) {
        this.email = member.getEmail();
        this.username = member.getUsername();
        this.createdAt = member.getCreatedAt();
    }
}
