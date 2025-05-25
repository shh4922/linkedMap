package com.hyeonho.linkedmap.room.repository;

import com.hyeonho.linkedmap.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByIdAndDeletedAtIsNull(Long categoryId);
}
