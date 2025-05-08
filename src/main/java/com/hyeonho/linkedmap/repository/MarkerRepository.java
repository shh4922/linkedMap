package com.hyeonho.linkedmap.repository;

import com.hyeonho.linkedmap.entity.Marker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MarkerRepository extends JpaRepository<Marker, Long> {

    /** 마커리스트 조회. 삭제된건 조회하지않음. */
    @Query("SELECT ma FROM Marker ma WHERE ma.room.id = :roomId AND ma.deletedAt IS NULL")
    List<Marker> getMarkerList(@Param("roomId") Long roomId);

    Optional<Marker> findByIdAndDeletedAtIsNull(Long id);

    Long countByRoomId(Long roomId);
}
