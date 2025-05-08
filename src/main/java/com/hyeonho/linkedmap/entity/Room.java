package com.hyeonho.linkedmap.entity;

import com.hyeonho.linkedmap.data.request.room.RoomUpdateRequest;
import com.hyeonho.linkedmap.data.request.room.CreateRoomRequest;
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

    @Column(nullable = true, length = 100)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member currentOwner; // 현재 방장 정보

    @ManyToOne
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member creator; // 카테고리를 만든사람

    @Column(nullable = true, length = 500)
    private String description;

    /** 카테고리의 상태 - 활성화, deletePending, delete */
    @Enumerated(EnumType.STRING)
    @Column
    private RoomState roomState;

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
        this.roomState = RoomState.ACTIVE;
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
