package com.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.dto.request.GuestHouseDetailRequest;
import com.backend.dto.request.GuestHouseRoomRequest;
import com.backend.dto.request.RoomResponseRequest;
import com.backend.dto.response.ReviewResponse;
import com.backend.entity.Guesthouse;
import com.backend.entity.Reservation;
import com.backend.entity.Review;
import com.backend.entity.Room;
import com.backend.entity.User;
import com.backend.repository.GuesthouseRepository;
import com.backend.repository.ReviewRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationInfoService 테스트")
class ReservationInfoServiceTest {
    @Mock
    private GuesthouseRepository guestHouseRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReservationInfoService reservationInfoService;

    private Guesthouse testGuesthouse;
    private Room testRoom1;
    private Room testRoom2;
    private Room testRoom3;
    private Review testReview1;
    private Review testReview2;
    private User testHost;
    private User testGuest;
    private Reservation testReservation1;
    private Reservation testReservation2;

    @BeforeEach
    void setUp() {
        // Test Host 설정
        testHost = User.builder()
                .id(1)
                .loginId("host")
                .username("호스트")
                .role("HOST")
                .build();

        // Test Guest 설정
        testGuest = User.builder()
                .id(2)
                .loginId("guest")
                .username("게스트")
                .role("GUEST")
                .build();

        // Test Guesthouse 설정
        testGuesthouse = Guesthouse.builder()
                .id(1)
                .host(testHost)
                .name("테스트 게스트하우스")
                .description("아름다운 게스트하우스입니다")
                .address("서울시 강남구 테스트로 123")
                .rating(4.5)
                .phoneNumber("02-1234-5678")
                .photoId(100)
                .roomCount(3)
                .build();

        // Test Rooms 설정
        testRoom1 = Room.builder()
                .id(1)
                .guesthouse(testGuesthouse)
                .name("디럭스 룸")
                .capacity(2)
                .price(100000)
                .photoId(101)
                .build();

        testRoom2 = Room.builder()
                .id(2)
                .guesthouse(testGuesthouse)
                .name("스탠다드 룸")
                .capacity(4)
                .price(80000)
                .photoId(102)
                .build();

        testRoom3 = Room.builder()
                .id(3)
                .guesthouse(testGuesthouse)
                .name("이코노미 룸")
                .capacity(6)
                .price(60000)
                .photoId(103)
                .build();

        // 게스트하우스에 방 목록 설정
        testGuesthouse.getRoomList().addAll(Arrays.asList(testRoom1, testRoom2, testRoom3));

        // Test Reservations 설정
        testReservation1 = Reservation.builder()
                .id(1)
                .guest(testGuest)
                .room(testRoom1)
                .build();

        testReservation2 = Reservation.builder()
                .id(2)
                .guest(testGuest)
                .room(testRoom2)
                .build();

        // Test Reviews 설정
        testReview1 = Review.builder()
                .id(1)
                .reservation(testReservation1)
                .rating(5)
                .comment("정말 좋은 숙소였습니다!")
                .createdAt(LocalDateTime.of(2024, 1, 15, 10, 30))
                .build();

        testReview2 = Review.builder()
                .id(2)
                .reservation(testReservation2)
                .rating(4)
                .comment("깔끔하고 편안했어요")
                .createdAt(LocalDateTime.of(2024, 2, 20, 14, 45))
                .build();
    }

    @Test
    @DisplayName("게스트하우스 상세정보 조회 성공")
    void getGuestHouseDetail_Success() {
        // given
        given(guestHouseRepository.findById(1)).willReturn(Optional.of(testGuesthouse));

        // when
        GuestHouseDetailRequest result = reservationInfoService.getGuestHouseDetail(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("테스트 게스트하우스");
        assertThat(result.getDescription()).isEqualTo("아름다운 게스트하우스입니다");
        assertThat(result.getAddress()).isEqualTo("서울시 강남구 테스트로 123");
        assertThat(result.getRating()).isEqualTo(4.5);
        assertThat(result.getPhotoId()).isEqualTo(100);
        assertThat(result.getRoom_count()).isEqualTo(3);

        verify(guestHouseRepository).findById(1);
    }

    @Test
    @DisplayName("게스트하우스 상세정보 조회 실패 - 존재하지 않는 ID")
    void getGuestHouseDetail_NotFound() {
        // given
        given(guestHouseRepository.findById(999)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationInfoService.getGuestHouseDetail(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Guesthouse not found with id: 999");

        verify(guestHouseRepository).findById(999);
    }

    @Test
    @DisplayName("게스트하우스 방 목록 조회 성공 - 필터 없음")
    void getGuestHouseRooms_Success_NoFilter() {
        // given
        given(guestHouseRepository.findById(1)).willReturn(Optional.of(testGuesthouse));

        // when
        RoomResponseRequest result = reservationInfoService.getGuestHouseRooms(1, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRooms()).hasSize(3);
        
        List<GuestHouseRoomRequest> rooms = result.getRooms();
        
        // 첫 번째 방 검증
        GuestHouseRoomRequest room1 = rooms.get(0);
        assertThat(room1.getId()).isEqualTo(1);
        assertThat(room1.getName()).isEqualTo("디럭스 룸");
        assertThat(room1.getCapacity()).isEqualTo(2);
        assertThat(room1.getPrice()).isEqualTo(100000);
        assertThat(room1.getPhoto_id()).isEqualTo(101);

        // 두 번째 방 검증
        GuestHouseRoomRequest room2 = rooms.get(1);
        assertThat(room2.getId()).isEqualTo(2);
        assertThat(room2.getName()).isEqualTo("스탠다드 룸");
        assertThat(room2.getCapacity()).isEqualTo(4);

        // 세 번째 방 검증
        GuestHouseRoomRequest room3 = rooms.get(2);
        assertThat(room3.getId()).isEqualTo(3);
        assertThat(room3.getName()).isEqualTo("이코노미 룸");
        assertThat(room3.getCapacity()).isEqualTo(6);

        verify(guestHouseRepository).findById(1);
    }

    @Test
    @DisplayName("게스트하우스 방 목록 조회 성공 - 필터 적용")
    void getGuestHouseRooms_Success_WithFilter() {
        // given
        given(guestHouseRepository.findById(1)).willReturn(Optional.of(testGuesthouse));
        List<Integer> roomFilter = Arrays.asList(1, 3); // 1번, 3번 방만 필터링

        // when
        RoomResponseRequest result = reservationInfoService.getGuestHouseRooms(1, roomFilter);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRooms()).hasSize(2);
        
        List<GuestHouseRoomRequest> rooms = result.getRooms();
        assertThat(rooms.get(0).getId()).isEqualTo(1);
        assertThat(rooms.get(1).getId()).isEqualTo(3);
        
        // 2번 방은 필터링되어 포함되지 않음
        assertThat(rooms.stream().anyMatch(room -> room.getId() == 2)).isFalse();

        verify(guestHouseRepository).findById(1);
    }

    @Test
    @DisplayName("게스트하우스 방 목록 조회 성공 - 빈 필터")
    void getGuestHouseRooms_Success_EmptyFilter() {
        // given
        given(guestHouseRepository.findById(1)).willReturn(Optional.of(testGuesthouse));
        List<Integer> emptyFilter = Arrays.asList();

        // when
        RoomResponseRequest result = reservationInfoService.getGuestHouseRooms(1, emptyFilter);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRooms()).hasSize(3); // 빈 필터는 전체 조회와 동일

        verify(guestHouseRepository).findById(1);
    }

    @Test
    @DisplayName("게스트하우스 방 목록 조회 실패 - 존재하지 않는 게스트하우스")
    void getGuestHouseRooms_NotFound() {
        // given
        given(guestHouseRepository.findById(999)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationInfoService.getGuestHouseRooms(999, null))
                .isInstanceOf(EntityNotFoundException.class);

        verify(guestHouseRepository).findById(999);
    }

    @Test
    @DisplayName("리뷰 목록 조회 성공")
    void getReview_Success() {
        // given
        given(guestHouseRepository.existsById(1)).willReturn(true);
        given(reviewRepository.findByGuesthouseId(1)).willReturn(Arrays.asList(testReview1, testReview2));

        // when
        List<ReviewResponse> result = reservationInfoService.getReview(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        // 첫 번째 리뷰 검증
        ReviewResponse review1 = result.get(0);
        assertThat(review1.getId()).isEqualTo(1);
        assertThat(review1.getReservation_id()).isEqualTo(1);
        assertThat(review1.getRating()).isEqualTo(5);
        assertThat(review1.getComment()).isEqualTo("정말 좋은 숙소였습니다!");
        assertThat(review1.getCreated_at()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30));

        // 두 번째 리뷰 검증
        ReviewResponse review2 = result.get(1);
        assertThat(review2.getId()).isEqualTo(2);
        assertThat(review2.getReservation_id()).isEqualTo(2);
        assertThat(review2.getRating()).isEqualTo(4);
        assertThat(review2.getComment()).isEqualTo("깔끔하고 편안했어요");
        assertThat(review2.getCreated_at()).isEqualTo(LocalDateTime.of(2024, 2, 20, 14, 45));

        verify(guestHouseRepository).existsById(1);
        verify(reviewRepository).findByGuesthouseId(1);
    }

    @Test
    @DisplayName("리뷰 목록 조회 성공 - 리뷰 없는 경우")
    void getReview_Success_NoReviews() {
        // given
        given(guestHouseRepository.existsById(1)).willReturn(true);
        given(reviewRepository.findByGuesthouseId(1)).willReturn(Arrays.asList());

        // when
        List<ReviewResponse> result = reservationInfoService.getReview(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(guestHouseRepository).existsById(1);
        verify(reviewRepository).findByGuesthouseId(1);
    }

    @Test
    @DisplayName("리뷰 목록 조회 실패 - 존재하지 않는 게스트하우스")
    void getReview_NotFound() {
        // given
        given(guestHouseRepository.existsById(999)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> reservationInfoService.getReview(999))
                .isInstanceOf(EntityNotFoundException.class);

        verify(guestHouseRepository).existsById(999);
        verify(reviewRepository, never()).findByGuesthouseId(anyInt());
    }

    @Test
    @DisplayName("방 필터링 로직 테스트 - 존재하지 않는 방 ID 포함")
    void getGuestHouseRooms_FilterNonExistentRooms() {
        // given
        given(guestHouseRepository.findById(1)).willReturn(Optional.of(testGuesthouse));
        List<Integer> roomFilter = Arrays.asList(1, 999); // 999번 방은 존재하지 않음

        // when
        RoomResponseRequest result = reservationInfoService.getGuestHouseRooms(1, roomFilter);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRooms()).hasSize(1); // 1번 방만 포함
        assertThat(result.getRooms().get(0).getId()).isEqualTo(1);

        verify(guestHouseRepository).findById(1);
    }

    @Test
    @DisplayName("게스트하우스에 방이 없는 경우")
    void getGuestHouseRooms_NoRooms() {
        // given
        Guesthouse emptyGuesthouse = Guesthouse.builder()
                .id(2)
                .name("빈 게스트하우스")
                .roomCount(0)
                .build();
        
        given(guestHouseRepository.findById(2)).willReturn(Optional.of(emptyGuesthouse));

        // when
        RoomResponseRequest result = reservationInfoService.getGuestHouseRooms(2, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRooms()).isEmpty();

        verify(guestHouseRepository).findById(2);
    }
}