package com.hyeonho.linkedmap.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "category_user")
public class CategoryUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;

    /** 만든사람 */
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member owner;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;


//    @Enumerated
//    @Column(name = "invited_state")
//    private InviteState invitedState;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;


    /**
     *  역할 테이블 따로 만들고임
     *  admin, manager, user, readOnly, ...
     */
//    @ManyToOne
//    private CategoryUserRole categoryUserRole;

}
