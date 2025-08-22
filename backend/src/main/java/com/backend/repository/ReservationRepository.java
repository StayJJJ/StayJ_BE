package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
	@EntityGraph(attributePaths = {"room", "room.guesthouse", "guest"})
	List<Reservation> findAllByRoom_Guesthouse_IdOrderByCheckInDateAsc(Integer guesthouseId);
}
