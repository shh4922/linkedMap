package com.hyeonho.linkedmap.repository;

import com.hyeonho.linkedmap.entity.Marker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkerRepository extends JpaRepository<Marker, Long> {
}
