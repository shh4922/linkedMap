package com.hyeonho.linkedmap.member.member;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hyeonho.linkedmap.member.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberInfoDTO {
    private Long memberId;
    private String email;
    private String username;
    private String role;
    private String profileImage;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Builder
    public MemberInfoDTO(Long memberId, String email, String username, String role, String profileImage, LocalDateTime createdAt) {
        this.memberId = memberId;
        this.email = email;
        this.username = username;
        this.role = role;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
    }

    public static MemberInfoDTO fromEntity(Member member) {
        return MemberInfoDTO.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .memberId(member.getId())
                .role(member.getRole().name())
                .profileImage(member.getProfileImage())
                .createdAt(member.getCreatedAt())
                .build();
    }


}
