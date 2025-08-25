package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.backend.entity.Reservation;
import com.backend.entity.Review;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Query("SELECT r FROM Review r " +
           "JOIN r.reservation res " +
           "JOIN res.room room " +
           "WHERE room.guestHouse.id = :guesthouseId " +
           "ORDER BY r.createdAt DESC")
    List<Review> findByGuesthouseId(@Param("guesthouseId") Integer guesthouseId);
    boolean existsByReservation(Reservation reservation);
}