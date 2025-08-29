package com.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.backend.dto.request.ReviewCreateRequest;
import com.backend.dto.request.ReviewUpdateRequest;
import com.backend.dto.response.ReviewResponseDto;
import com.backend.entity.Guesthouse;
import com.backend.entity.Reservation;
import com.backend.entity.Review;
import com.backend.entity.Room;
import com.backend.entity.User;
import com.backend.repository.GuesthouseRepository;
import com.backend.repository.ReservationRepository;
import com.backend.repository.ReviewRepository;
import com.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GuesthouseRepository guesthouseRepository;
    
    @InjectMocks
    private ReviewService reviewService;

    private User guestUser;
    private Guesthouse guesthouse;
    private Room room;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        guestUser = new User();
        guestUser.setId(1);

        guesthouse = Guesthouse.builder()
                .id(1)
                .name("My Guesthouse")
                .build();

        room = Room.builder()
                .id(1)
                .guesthouse(guesthouse)
                .build();

        reservation = Reservation.builder()
                .id(100)
                .guest(guestUser)
                .room(room)  // <-- assign the room here
                .checkOutDate(LocalDate.now().minusDays(1)) // 체크아웃 완료된 상태
                .build();
    }

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReview_success() {
        // given
        ReviewCreateRequest request = new ReviewCreateRequest(100, 5, "좋았습니다!");

        when(reservationRepository.findById(100)).thenReturn(Optional.of(reservation));
        when(reviewRepository.existsByReservation(reservation)).thenReturn(false);

        Review savedReview = Review.builder()
                .id(10)
                .reservation(reservation)
                .rating(5)
                .comment("좋았습니다!")
                .createdAt(LocalDateTime.now())
                .build();

        when(guesthouseRepository.findById(guesthouse.getId())).thenReturn(Optional.of(guesthouse));
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        // when
        ReviewResponseDto response = reviewService.createReview(1, request);

        // then
        assertNotNull(response);
        assertEquals(10, response.getId());
        assertEquals(100, response.getReservationId());
        assertEquals(5, response.getRating());
        assertEquals("좋았습니다!", response.getComment());

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 예약이 존재하지 않음")
    void createReview_reservationNotFound() {
        // given
        ReviewCreateRequest request = new ReviewCreateRequest(999, 5, "후기");

        when(reservationRepository.findById(999)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResponseStatusException.class,
                () -> reviewService.createReview(1, request));
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 다른 유저가 시도")
    void createReview_forbiddenUser() {
        // given
        ReviewCreateRequest request = new ReviewCreateRequest(100, 4, "후기");

        when(reservationRepository.findById(100)).thenReturn(Optional.of(reservation));

        // 다른 사용자
        int otherUserId = 2;

        // when & then
        assertThrows(ResponseStatusException.class,
                () -> reviewService.createReview(otherUserId, request));
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 체크아웃 전")
    void createReview_checkOutNotFinished() {
        // given
        reservation.setCheckOutDate(LocalDate.now().plusDays(1));
        ReviewCreateRequest request = new ReviewCreateRequest(100, 5, "후기");

        when(reservationRepository.findById(100)).thenReturn(Optional.of(reservation));

        // when & then
        assertThrows(ResponseStatusException.class,
                () -> reviewService.createReview(1, request));
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 이미 리뷰 존재")
    void createReview_duplicateReview() {
        // given
        ReviewCreateRequest request = new ReviewCreateRequest(100, 5, "후기");

        when(reservationRepository.findById(100)).thenReturn(Optional.of(reservation));
        when(reviewRepository.existsByReservation(reservation)).thenReturn(true);

        // when & then
        assertThrows(ResponseStatusException.class,
                () -> reviewService.createReview(1, request));
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview_success() {
        // given
        Review review = Review.builder()
                .id(10)
                .reservation(reservation)
                .rating(3)
                .comment("보통이었음")
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewRepository.findById(10)).thenReturn(Optional.of(review));

        ReviewUpdateRequest request = new ReviewUpdateRequest(5, "아주 좋음!");

        // when
        boolean result = reviewService.updateReview(1, 10, request);

        // then
        assertTrue(result);
        assertEquals(5, review.getRating());
        assertEquals("아주 좋음!", review.getComment());
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 작성자가 아님")
    void updateReview_forbiddenUser() {
        // given
        Review review = Review.builder()
                .id(10)
                .reservation(reservation)
                .rating(3)
                .comment("보통")
                .build();

        when(reviewRepository.findById(10)).thenReturn(Optional.of(review));

        ReviewUpdateRequest request = new ReviewUpdateRequest(4, "괜찮음");

        // when
        boolean result = reviewService.updateReview(2, 10, request);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReview_success() {
        // given
        Review review = Review.builder()
                .id(10)
                .reservation(reservation)
                .rating(4)
                .comment("삭제할 리뷰")
                .build();

        when(reviewRepository.findById(10)).thenReturn(Optional.of(review));

        // when
        boolean result = reviewService.deleteReview(1, 10);

        // then
        assertTrue(result);
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    @DisplayName("리뷰 단건 조회 성공")
    void getReviewById_success() {
        // given
        Review review = Review.builder()
                .id(10)
                .reservation(reservation)
                .rating(4)
                .comment("조회할 리뷰")
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewRepository.findById(10)).thenReturn(Optional.of(review));

        // when
        ReviewResponseDto response = reviewService.getReviewById(10);

        // then
        assertNotNull(response);
        assertEquals(10, response.getId());
        assertEquals(100, response.getReservationId());
        assertEquals(4, response.getRating());
    }
}
