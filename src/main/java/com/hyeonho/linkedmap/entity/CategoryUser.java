package com.hyeonho.linkedmap.entity;

import com.hyeonho.linkedmap.enumlist.CategoryUserRole;
import com.hyeonho.linkedmap.enumlist.InviteState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서 기본 생성자 호출 불가
@Table(name = "category_user")
public class CategoryUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 속한 카테고리 */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;

    /** 속한 유저 */
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    /** 초대 상태 */
    @Enumerated
    @Column(name = "invited_state")
    private InviteState invitedState;

    /** 속한 카테고리 에서 권한 */
    @Enumerated(EnumType.STRING)
    @Column(name = "category_user_role")
    private CategoryUserRole categoryUserRole;

    /** 카테고리의 상태 - 활성화, 삭제된카테고리 */
    @Enumerated(EnumType.STRING)
    @Column(name = "category_state")
    private CategoryState categoryState;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Builder
    public CategoryUser(Category category, Member member, InviteState invitedState, CategoryUserRole categoryUserRole, CategoryState categoryState) {
        this.category = category;
        this.member = member;
        this.invitedState = invitedState;
        this.categoryUserRole = categoryUserRole;
        this.categoryState = categoryState;
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

}
