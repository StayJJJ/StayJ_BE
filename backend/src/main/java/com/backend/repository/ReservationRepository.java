package com.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.entity.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
	List<Reservation> findByRoomId(Integer roomId);

	List<Reservation> findByGuest_Id(Integer userId);

	@EntityGraph(attributePaths = { "room", "room.guesthouse", "guest" })
	List<Reservation> findAllByRoom_Guesthouse_IdOrderByCheckInDateAsc(Integer guesthouseId);

	@Query("""
			    select coalesce(sum(r.peopleCount), 0)
			    from Reservation r
			    where r.room.id = :roomId
			      and r.checkInDate < :checkOut
			      and r.checkOutDate > :checkIn
			""")
	int sumPeopleCountForOverlap(@Param("roomId") Integer roomId, @Param("checkIn") LocalDate checkIn,
			@Param("checkOut") LocalDate checkOut);
}
