package com.hyeonho.linkedmap.data.dto.member;

import com.hyeonho.linkedmap.data.request.MemberUpdateRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class MemberUpdateDto {

    private String email;
    private String username;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    @Builder
    public MemberUpdateDto(String email, String username, String profileImage, LocalDateTime createdAt, LocalDateTime updateAt) {
        this.email = email;
        this.username = username;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }
}
