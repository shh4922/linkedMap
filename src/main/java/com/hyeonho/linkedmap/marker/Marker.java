package com.hyeonho.linkedmap.marker;

import com.hyeonho.linkedmap.data.request.CreateMarkerRequest;
import com.hyeonho.linkedmap.data.request.marker.UpdateMarkerRequest;
import com.hyeonho.linkedmap.member.Member;
import com.hyeonho.linkedmap.room.entity.Room;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "marker")
public class Marker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 11, scale = 8)
    private BigDecimal lat;

    @Column(precision = 11, scale = 8)
    private BigDecimal lng;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String storeType;

    @Column(length = 100)
    private String address;

    @Column(length = 100)
    private String roadAddress;

    @Column(length = 500)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "create_user", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Room room;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt; // 누가 업데이트했는지도 확인해야하는데..

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Builder
    public Marker(CreateMarkerRequest request, Member member, Room room, String imageUrl) {
        this.lat = request.getLat();
        this.lng = request.getLng();
        this.title = request.getTitle();
        this.description = request.getDescription(); // Optional 아니면 이게 정답
        this.storeType = request.getStoreType();
        this.address = request.getAddress();
        this.roadAddress = request.getRoadAddress();
        this.imageUrl = imageUrl;
        this.member = member;
        this.room = room;
    }


    public void update(UpdateMarkerRequest req) {
        if (req.getTitle() != null) {
            this.title = req.getTitle();
        }
        if (req.getDescription() != null) {
            this.description = req.getDescription();
        }
        if (req.getStoreType() != null) {
            this.storeType = req.getStoreType();
        }

    }

    public void uploadImage(String url) {
        this.imageUrl = url;
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

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

}
