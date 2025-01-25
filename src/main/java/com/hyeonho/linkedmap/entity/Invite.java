package com.hyeonho.linkedmap.entity;

import com.hyeonho.linkedmap.enumlist.InviteState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "invite")
public class Invite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invitor", nullable = false, length = 50)
    private String invitor;

    @Column(name = "invited_member", length = 50)
    private String invitedMember;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt; // 파기날짜

    @Enumerated(EnumType.STRING)
    @Column(name = "invite_state", nullable = false)
    private InviteState inviteState;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
