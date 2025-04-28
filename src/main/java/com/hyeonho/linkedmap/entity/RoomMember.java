package com.hyeonho.linkedmap.entity;

import com.hyeonho.linkedmap.enumlist.RoomMemberRole;
import com.hyeonho.linkedmap.enumlist.InviteState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서 기본 생성자 호출 불가
@Table(name = "category_user")
public class RoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 속한 방 */
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Room room;

    /** 속한 유저 */
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    /** 초대 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "invited_state")
    private InviteState inviteState;

    /** 속한 카테고리 에서 권한 */
    @Enumerated(EnumType.STRING)
    @Column(name = "category_user_role")
    private RoomMemberRole roomMemberRole;

    /** 카테고리의 상태 - 활성화, deletePending, delete */
//    @Enumerated(EnumType.STRING)
//    @Column(name = "category_state")
//    private CategoryState categoryState;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Builder
    public RoomMember(Room room, Member member, InviteState inviteState, RoomMemberRole roomMemberRole) {
        this.room = room;
        this.member = member;
        this.inviteState = inviteState;
        this.roomMemberRole = roomMemberRole;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateInviteState(InviteState inviteState) {
        this.inviteState = inviteState;
    }

}
