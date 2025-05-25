package com.hyeonho.linkedmap.room.entity;

import com.hyeonho.linkedmap.data.request.room.RoomUpdateRequest;
import com.hyeonho.linkedmap.data.request.room.CreateRoomRequest;
import com.hyeonho.linkedmap.member.Member;
import com.hyeonho.linkedmap.room.RoomState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서 기본 생성자 호출 불가
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 100)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_owner_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member currentOwner; // 현재 방장 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member creator; // 방을 만든 사람

    @Column(nullable = true, length = 500)
    private String description;

    /** 방 상태 - 활성화, deletePending, delete */
    @Enumerated(EnumType.STRING)
    @Column
    private RoomState roomState = RoomState.ACTIVE;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    @Builder
    public Room(Member member, CreateRoomRequest request) {
        this.creator = member;
        this.currentOwner = member;
        this.name = request.getRoomName();
        if(request.getDescription() != null) {
            this.description = request.getDescription();
        }
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

    public void update(RoomUpdateRequest req) {
        if (req.getRoomName() != null) {
            this.name = req.getRoomName();
        }
        if(req.getImageUrl() != null) {
            this.imageUrl = req.getImageUrl();
        }
        if(req.getDescription() != null) {
            this.description = req.getDescription();
        }
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.roomState = RoomState.DELETE;
    }
}
