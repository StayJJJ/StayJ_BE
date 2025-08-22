package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
  List<Reservation> findByRoomId(Integer roomId);
  
	@EntityGraph(attributePaths = {"room", "room.guesthouse", "guest"})
	List<Reservation> findAllByRoom_Guesthouse_IdOrderByCheckInDateAsc(Integer guesthouseId);
}