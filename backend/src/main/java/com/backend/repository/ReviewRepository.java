package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entity.Reservation;
import com.backend.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // 예약당 리뷰가 존재하는지 확인
    boolean existsByReservation(Reservation reservation);
}
