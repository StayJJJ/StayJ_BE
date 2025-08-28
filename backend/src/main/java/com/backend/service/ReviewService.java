package com.backend.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.backend.dto.request.ReviewCreateRequest;
import com.backend.dto.request.ReviewUpdateRequest;
import com.backend.dto.response.ReviewResponseDto;
import com.backend.entity.Guesthouse;
import com.backend.entity.Reservation;
import com.backend.entity.Review;
import com.backend.repository.GuesthouseRepository;
import com.backend.repository.ReservationRepository;
import com.backend.repository.ReviewRepository;
import com.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final GuesthouseRepository guesthouseRepository;

    @Transactional
    public ReviewResponseDto createReview(Integer userId, ReviewCreateRequest request) {    	
    	Reservation reservation = reservationRepository.findById(request.getReservationId())
    	        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "예약이 존재하지 않습니다."));

    	    if (!reservation.getGuest().getId().equals(userId)) {
    	        throw new ResponseStatusException(FORBIDDEN, "예약한 사용자만 리뷰 작성 가능");
    	    }
    	    if (reservation.getCheckOutDate().isAfter(LocalDate.now())) {
    	        throw new ResponseStatusException(BAD_REQUEST, "체크아웃 이후에만 리뷰 작성 가능합니다.");
    	    }
    	    if (reviewRepository.existsByReservation(reservation)) {
    	        throw new ResponseStatusException(CONFLICT, "이 예약에 대해 이미 리뷰가 작성되어 있습니다.");
    	    }

        Review review = Review.builder()
                .reservation(reservation)
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        Review savedReview = reviewRepository.save(review);
        
        // Reservation의 Rating 업데이트
        System.out.println("전 = " + reservation.getRoom().getGuesthouse().getRating());
        updateRating(reservation.getRoom().getGuesthouse().getId());
        System.out.println("후 = " + reservation.getRoom().getGuesthouse().getRating());

        // Builder 패턴으로 DTO 반환
        return ReviewResponseDto.builder()
                .id(savedReview.getId())
                .reservationId(reservation.getId())
                .rating(savedReview.getRating())
                .comment(savedReview.getComment())
                .createdAt(savedReview.getCreatedAt())
                .build();
    }
    
    @Transactional
    public void updateRating(Integer guesthouseId) {
        Double avg = reviewRepository.calculateAverageRating(guesthouseId);
        Guesthouse guesthouse = guesthouseRepository.findById(guesthouseId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "게스트하우스가 존재하지 않습니다."));
        
        guesthouse.updateRating(avg != null ? Math.round(avg * 10) / 10.0 : 0.0);
        guesthouseRepository.save(guesthouse); 
    }

    @Transactional
    public boolean updateReview(Integer userId, int reviewId, ReviewUpdateRequest request) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isEmpty()) return false;

        Review review = reviewOpt.get();

        if (!review.getReservation().getGuest().getId().equals(userId)) return false;

        // 엔티티의 updateReview() 사용
        review.updateReview(request.getRating(), request.getComment());
        reviewRepository.save(review);
        return true;
    }

    @Transactional
    public boolean deleteReview(Integer userId, int reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰가 존재하지 않습니다."));

        Reservation reservation = review.getReservation();
        reservation.deleteReview();

        reviewRepository.delete(review);
        return true;
    }
    
    // 리뷰 단건 조회
    public ReviewResponseDto getReviewById(int reviewId) {
        return reviewRepository.findById(reviewId)
                .map(review -> ReviewResponseDto.builder()
                        .id(review.getId())
                        .reservationId(review.getReservation().getId())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .createdAt(review.getCreatedAt())
                        .build()
                )
                .orElse(null);
    }
}
