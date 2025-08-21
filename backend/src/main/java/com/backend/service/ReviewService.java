package com.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.dto.request.ReviewCreateRequest;
import com.backend.dto.request.ReviewUpdateRequest;
import com.backend.dto.response.ReviewResponseDto;
import com.backend.entity.Reservation;
import com.backend.entity.Review;
import com.backend.entity.User;
import com.backend.repository.ReservationRepository;
import com.backend.repository.ReviewRepository;
import com.backend.repository.UserRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         ReservationRepository reservationRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReviewResponseDto createReview(Integer userId, ReviewCreateRequest request) {
        // 1. 예약 존재 여부 확인
        Optional<Reservation> reservationOpt = reservationRepository.findById(request.getReservationId());
        if (reservationOpt.isEmpty()) {
            throw new IllegalArgumentException("예약이 존재하지 않습니다.");
        }
        Reservation reservation = reservationOpt.get();

        // 2. 예약한 사용자와 userId가 일치하는지 확인
        if (!reservation.getGuest().getId().equals(userId)) {
            throw new IllegalArgumentException("예약한 사용자만 리뷰 작성 가능");
        }

        // 3. 체크아웃 이후만 리뷰 작성 가능
        if (reservation.getCheckOutDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("체크아웃 이후에만 리뷰 작성이 가능합니다.");
        }

        // 4. 예약당 1개의 리뷰만 작성 가능
        if (reviewRepository.existsByReservation(reservation)) {
            throw new IllegalStateException("이 예약에 대해 이미 리뷰가 작성되어 있습니다.");
        }

        // 5. 리뷰 엔티티 생성 및 저장
        Review review = Review.builder()
                .reservation(reservation)
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        Review savedReview = reviewRepository.save(review);

        // 6. 응답 DTO 반환
        return new ReviewResponseDto(
                savedReview.getId(),
                reservation.getId(),
                savedReview.getRating(),
                savedReview.getComment(),
                savedReview.getCreatedAt()
        );
    }

    @Transactional
    public boolean updateReview(Integer userId, int reviewId, ReviewUpdateRequest request) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isEmpty()) {
            return false;
        }
        Review review = reviewOpt.get();

        // 본인 확인
        if (!review.getReservation().getGuest().getId().equals(userId)) {
            return false;
        }

        // 업데이트
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        reviewRepository.save(review);
        return true;
    }

    @Transactional
    public boolean deleteReview(Integer userId, int reviewId) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isEmpty()) {
            return false;
        }
        Review review = reviewOpt.get();

        // 본인 확인
        if (!review.getReservation().getGuest().getId().equals(userId)) {
            return false;
        }

        reviewRepository.delete(review);
        return true;
    }
}
