package com.hyeonho.linkedmap.entity;

import com.hyeonho.linkedmap.enumlist.InviteState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "invite")
public class Invite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "invitor", nullable = false)
    private Long invitor;

    @Column(name = "invited_member", nullable = true)
    private String invitedMember;

    @Column(nullable = false, unique = true)
    private UUID inviteKey;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt; // 파기날짜

    @Enumerated(EnumType.STRING)
    @Column(name = "invite_state", nullable = false)
    private InviteState inviteState;

    //    @ManyToOne
//    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
//    private Category category;

    //    @ManyToOne
//    @JoinColumn(name = "invitor", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
//    private Member invitor;
//
//    @ManyToOne
//    @JoinColumn(name = "invited_member", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
//    private Member invitedMember;

    @Builder
    public Invite(Long roomId, Long invitor) {
        this.roomId = roomId;
        this.invitor = invitor;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.inviteKey = UUID.randomUUID();
        this.expireAt = createdAt.plusMinutes(5);
        this.inviteState = InviteState.PENDING;
    }
}
