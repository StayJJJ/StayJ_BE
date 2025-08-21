package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Integer> {
}
