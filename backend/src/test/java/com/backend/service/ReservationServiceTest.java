package com.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.dto.request.ReservationRequest;
import com.backend.dto.response.ReservationResponse;
import com.backend.entity.Guesthouse;
import com.backend.entity.Reservation;
import com.backend.entity.Review;
import com.backend.entity.Room;
import com.backend.entity.User;
import com.backend.repository.ReservationRepository;
import com.backend.repository.RoomRepository;
import com.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService 테스트")
class ReservationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private ReservationService reservationService;

    private User testUser;
    private Room testRoom;
    private Guesthouse testGuesthouse;
    private ReservationRequest reservationRequest;
    private Reservation existingReservation;

    @BeforeEach
    void setUp() {
        // Test User 설정
        testUser = User.builder()
                .id(1)
                .loginId("testuser")
                .username("테스트사용자")
                .password("password")
                .role("GUEST")
                .phoneNumber("010-1234-5678")
                .build();

        // Test Guesthouse 설정
        testGuesthouse = Guesthouse.builder()
                .id(1)
                .name("테스트게스트하우스")
                .description("테스트용 게스트하우스입니다")
                .address("서울시 강남구")
                .rating(4.5)
                .phoneNumber("02-1234-5678")
                .roomCount(5)
                .build();

        // Test Room 설정
        testRoom = Room.builder()
                .id(1)
                .guesthouse(testGuesthouse)
                .capacity(4)
                .build();

        // Test ReservationRequest 설정
        reservationRequest = ReservationRequest.builder()
                .roomId(1)
                .checkInDate(LocalDate.of(2024, 12, 20))
                .checkOutDate(LocalDate.of(2024, 12, 22))
                .peopleCount(2)
                .build();

        // 기존 예약 설정
        existingReservation = Reservation.builder()
                .id(1)
                .guest(testUser)
                .room(testRoom)
                .checkInDate(LocalDate.of(2024, 12, 15))
                .checkOutDate(LocalDate.of(2024, 12, 18))
                .peopleCount(2)
                .build();
    }

    @Test
    @DisplayName("예약 생성 성공 - 정상적인 예약")
    void createReservation_Success() {
        // given
        given(userRepository.findById(1)).willReturn(Optional.of(testUser));
        given(roomRepository.findById(1)).willReturn(Optional.of(testRoom));
        given(reservationRepository.sumPeopleCountForOverlap(anyInt(), any(), any()))
                .willReturn(0); // 기존 예약 없음 → 정원 다 사용 가능

        // when
        boolean result = reservationService.createReservation(1, reservationRequest);

        // then
        assertThat(result).isTrue();
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("예약 생성 실패 - 존재하지 않는 사용자")
    void createReservation_UserNotFound() {
        // given
        given(userRepository.findById(1)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(1, reservationRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("예약 생성 실패 - 존재하지 않는 방")
    void createReservation_RoomNotFound() {
        // given
        given(userRepository.findById(1)).willReturn(Optional.of(testUser));
        given(roomRepository.findById(1)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(1, reservationRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Room not found");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("예약 생성 실패 - 인원 초과")
    void createReservation_ExceedsCapacity() {
        // given
        ReservationRequest overCapacityRequest = ReservationRequest.builder()
                .roomId(1)
                .checkInDate(LocalDate.of(2024, 12, 20))
                .checkOutDate(LocalDate.of(2024, 12, 22))
                .peopleCount(5) // 방 정원(4명)을 초과
                .build();

        given(userRepository.findById(1)).willReturn(Optional.of(testUser));
        given(roomRepository.findById(1)).willReturn(Optional.of(testRoom));
        given(reservationRepository.sumPeopleCountForOverlap(anyInt(), any(), any())).willReturn(0);

        // when
        boolean result = reservationService.createReservation(1, overCapacityRequest);

        // then
        assertThat(result).isFalse();
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("내 예약 목록 조회 성공")
    void getMyReservations_Success() {
        // given
        Review testReview = Review.builder()
                .id(1)
                .comment("좋은 숙소였습니다")
                .build();
        
        existingReservation.setReview(testReview);
        
        given(reservationRepository.findByGuest_Id(1)).willReturn(Arrays.asList(existingReservation));

        // when
        List<ReservationResponse> responses = reservationService.getMyReservations(1);

        // then
        assertThat(responses).hasSize(1);
        ReservationResponse response = responses.get(0);
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getRoomId()).isEqualTo(1);
        assertThat(response.getGuesthouseId()).isEqualTo(1);
        assertThat(response.getGuesthouseName()).isEqualTo("테스트게스트하우스");
        assertThat(response.getCheckInDate()).isEqualTo(LocalDate.of(2024, 12, 15));
        assertThat(response.getCheckOutDate()).isEqualTo(LocalDate.of(2024, 12, 18));
        assertThat(response.getPeopleCount()).isEqualTo(2);
        assertThat(response.getReviewId()).isEqualTo(1);
        assertThat(response.getReviewComment()).isEqualTo("좋은 숙소였습니다");
    }

    @Test
    @DisplayName("내 예약 목록 조회 성공 - 리뷰 없는 경우")
    void getMyReservations_NoReview() {
        // given
        given(reservationRepository.findByGuest_Id(1)).willReturn(Arrays.asList(existingReservation));

        // when
        List<ReservationResponse> responses = reservationService.getMyReservations(1);

        // then
        assertThat(responses).hasSize(1);
        ReservationResponse response = responses.get(0);
        assertThat(response.getReviewId()).isNull();
        assertThat(response.getReviewComment()).isNull();
    }

    @Test
    @DisplayName("예약 취소 성공 - 체크인 전")
    void cancelReservation_Success() {
        // given
        Reservation futureReservation = Reservation.builder()
                .id(1)
                .guest(testUser)
                .room(testRoom)
                .checkInDate(LocalDate.now().plusDays(5)) // 5일 후 체크인
                .checkOutDate(LocalDate.now().plusDays(7))
                .peopleCount(2)
                .build();

        given(reservationRepository.findById(1)).willReturn(Optional.of(futureReservation));

        // when
        boolean result = reservationService.cancelReservation(1, 1);

        // then
        assertThat(result).isTrue();
        verify(reservationRepository).delete(futureReservation);
    }

    @Test
    @DisplayName("예약 취소 실패 - 체크인 당일 이후")
    void cancelReservation_CheckInDatePassed() {
        // given
        Reservation pastReservation = Reservation.builder()
                .id(1)
                .guest(testUser)
                .room(testRoom)
                .checkInDate(LocalDate.now()) // 오늘 체크인
                .checkOutDate(LocalDate.now().plusDays(2))
                .peopleCount(2)
                .build();

        given(reservationRepository.findById(1)).willReturn(Optional.of(pastReservation));

        // when
        boolean result = reservationService.cancelReservation(1, 1);

        // then
        assertThat(result).isFalse();
        verify(reservationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("예약 취소 실패 - 다른 사용자의 예약")
    void cancelReservation_NotOwner() {
        // given
        User otherUser = User.builder()
                .id(2)
                .loginId("otheruser")
                .username("다른사용자")
                .build();

        Reservation otherUserReservation = Reservation.builder()
                .id(1)
                .guest(otherUser) // 다른 사용자의 예약
                .room(testRoom)
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(3))
                .peopleCount(2)
                .build();

        given(reservationRepository.findById(1)).willReturn(Optional.of(otherUserReservation));

        // when
        boolean result = reservationService.cancelReservation(1, 1); // userId=1로 시도

        // then
        assertThat(result).isFalse();
        verify(reservationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("예약 취소 실패 - 존재하지 않는 예약")
    void cancelReservation_ReservationNotFound() {
        // given
        given(reservationRepository.findById(1)).willReturn(Optional.empty());

        // when
        boolean result = reservationService.cancelReservation(1, 1);

        // then
        assertThat(result).isFalse();
        verify(reservationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("예약 중복 체크 - 경계값 테스트")
    void createReservation_BoundaryOverlapTest() {
        // given (12/15~12/18 기존 예약은 '겹치지 않음'으로 가정)
        ReservationRequest boundaryRequest = ReservationRequest.builder()
                .roomId(1)
                .checkInDate(LocalDate.of(2024, 12, 18))
                .checkOutDate(LocalDate.of(2024, 12, 20))
                .peopleCount(2)
                .build();

        given(userRepository.findById(1)).willReturn(Optional.of(testUser));
        given(roomRepository.findById(1)).willReturn(Optional.of(testRoom));

        // ✅ 서비스가 사용하는 메서드를 Stub (경계에서는 중복 0으로)
        given(reservationRepository.sumPeopleCountForOverlap(
                eq(1),
                eq(LocalDate.of(2024, 12, 18)),
                eq(LocalDate.of(2024, 12, 20))
        )).willReturn(0);

        // when
        boolean result = reservationService.createReservation(1, boundaryRequest);

        // then
        assertThat(result).isTrue();
        verify(reservationRepository).save(any(Reservation.class));
        // (선택) 실제 호출 파라미터 확인
        verify(reservationRepository).sumPeopleCountForOverlap(
                eq(1),
                eq(LocalDate.of(2024, 12, 18)),
                eq(LocalDate.of(2024, 12, 20))
        );
    }
}