package com.hyeonho.linkedmap.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "marker")
public class Marker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal lat;

    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal lng;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member memberId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category categoryId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt; // 누가 업데이트했는지도 확인해야하는데..

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

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
