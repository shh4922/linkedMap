package com.hyeonho.linkedmap.repository;

import com.hyeonho.linkedmap.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Room findByIdAndDeletedAtIsNull(Long categoryId);
}
