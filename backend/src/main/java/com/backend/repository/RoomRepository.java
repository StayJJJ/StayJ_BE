package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.backend.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
	@Query("select coalesce(max(r.photoId), 0) from Room r")
	Integer findMaxPhotoId();
}
