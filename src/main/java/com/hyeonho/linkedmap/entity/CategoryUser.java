package com.hyeonho.linkedmap.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CategoryUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;

    /** 만든사람 */
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member owner;

//    @ManyToOne
//    @Column(name = "invited_state")
//    private InviteState invitedState;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;


    /**
     *  역할 테이블 따로 만들고임
     *  admin, manager, user, readOnly, ...
     */
//    @ManyToOne
//    private CategoryUserRole categoryUserRole;

}
