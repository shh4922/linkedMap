package com.hyeonho.linkedmap.entity;


import com.hyeonho.linkedmap.data.request.MemberUpdateRequest;
import com.hyeonho.linkedmap.enumlist.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서 기본 생성자 호출 불가
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = true, length = 200)
    private String profileImage;


    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false)
    private Role role;

    @Builder
    public Member(String email, String password, String username, String profileImage, Role role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.profileImage = profileImage;
        this.role = role != null ? role : Role.ROLE_USER;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.role = Role.ROLE_USER;
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public void update(MemberUpdateRequest request) {
        if(request.getUsername()!= null) {
            this.username = request.getUsername();
        }
        if(request.getProfileImage() != null) {
            this.profileImage = request.getProfileImage();
        }
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}