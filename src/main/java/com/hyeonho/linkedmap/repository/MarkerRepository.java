package com.hyeonho.linkedmap.repository;

import com.hyeonho.linkedmap.entity.Marker;
import com.hyeonho.linkedmap.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MarkerRepository extends JpaRepository<Marker, Long> {
//    @Query("SELECT m FROM Marker m WHERE m.category.id = :categoryId")
//    List<Marker> findMarkerByCategoryId(Long categoryId);
}
