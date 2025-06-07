package com.hyeonho.linkedmap.member.member;

import com.hyeonho.linkedmap.data.request.MemberUpdateRequest;
import com.hyeonho.linkedmap.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class MemberUpdateDto {

    private Long memberId;
    private String email;
    private String username;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    @Builder
    public MemberUpdateDto(Long memberId, String email, String username, String profileImage, LocalDateTime createdAt, LocalDateTime updateAt) {
        this.memberId = memberId;
        this.email = email;
        this.username = username;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }

    public static MemberUpdateDto fromEntity(Member member) {
        return MemberUpdateDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .username(member.getUsername())
                .profileImage(member.getProfileImage())
                .createdAt(member.getCreatedAt())
                .updateAt(member.getUpdatedAt())
                .build();
    }
}
