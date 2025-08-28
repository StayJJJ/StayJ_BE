package com.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.web.server.ResponseStatusException;

import com.backend.dto.request.GuestHouseCreateRequest;
import com.backend.dto.response.ReservationListItemDto;
import com.backend.entity.Guesthouse;
import com.backend.entity.Reservation;
import com.backend.entity.Room;
import com.backend.entity.User;
import com.backend.repository.GuesthouseRepository;
import com.backend.repository.ReservationRepository;
import com.backend.repository.RoomRepository;
import com.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class GuesthouseServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GuesthouseRepository guesthouseRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private GuesthouseService guesthouseService;

    private User testHost;
    private GuestHouseCreateRequest createRequest;
    private Guesthouse testGuesthouse;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        testHost = User.builder()
                .id(1)
                .loginId("host")
                .username("호스트")
                .role("HOST")
                .build();

        createRequest = GuestHouseCreateRequest.builder()
                .name("Test Guesthouse")
                .description("Test Description")
                .address("Test Address")
                .rating(4.5)
                .phoneNumber("010-1234-5678")
                .photoId(1)
                .roomCount(2)
                .rooms(Arrays.asList(
                        GuestHouseCreateRequest.RoomRequest.builder()
                                .name("Room 1")
                                .capacity(2)
                                .price(50000)
                                .photoId(1)
                                .build(),
                        GuestHouseCreateRequest.RoomRequest.builder()
                                .name("Room 2")
                                .capacity(4)
                                .price(80000)
                                .photoId(2)
                                .build()
                ))
                .build();

        testRoom = Room.builder()
                .id(1)
                .name("Test Room")
                .capacity(2)
                .price(50000)
                .photoId(1)
                .build();

        testGuesthouse = Guesthouse.builder()
                .id(1)
                .name("Test Guesthouse")
                .description("Test Description")
                .address("Test Address")
                .rating(4.5)
                .phoneNumber("010-1234-5678")
                .photoId(1)
                .roomCount(2)
                .host(testHost)
                .build();
        testGuesthouse.addRoom(testRoom);
    }

    @Test
    @DisplayName("게스트하우스와 룸을 성공적으로 생성")
    void createGuestHouseWithRooms_Success() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testHost));
        
        // 더 간단한 방법: doAnswer 사용
        when(guesthouseRepository.save(any(Guesthouse.class))).thenAnswer(invocation -> {
            Guesthouse savedGuesthouse = invocation.getArgument(0);
            // ID가 설정된 새로운 객체 반환 (실제 DB 저장을 시뮬레이션)
            return Guesthouse.builder()
                    .id(1) // ID 설정
                    .name(savedGuesthouse.getName())
                    .description(savedGuesthouse.getDescription())
                    .address(savedGuesthouse.getAddress())
                    .rating(savedGuesthouse.getRating())
                    .phoneNumber(savedGuesthouse.getPhoneNumber())
                    .photoId(savedGuesthouse.getPhotoId())
                    .roomCount(savedGuesthouse.getRoomCount())
                    .host(savedGuesthouse.getHost())
                    .build();
        });

        // When
        Integer result = guesthouseService.createGuestHouseWithRooms(1, createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1);
        verify(userRepository).findById(1);
        verify(guesthouseRepository).save(any(Guesthouse.class));
    }

    @Test
    @DisplayName("존재하지 않는 호스트로 게스트하우스 생성 시 예외 발생")
    void createGuestHouseWithRooms_HostNotFound() {
        // Given
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> guesthouseService.createGuestHouseWithRooms(999, createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Host not found");
    }

    @Test
    @DisplayName("룸 개수와 실제 룸 배열 크기가 일치하지 않을 때 예외 발생")
    void createGuestHouseWithRooms_RoomCountMismatch() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testHost));
        
        GuestHouseCreateRequest invalidRequest = GuestHouseCreateRequest.builder()
                .name("Test Guesthouse")
                .roomCount(3) // 룸 개수는 3개
                .rooms(Arrays.asList( // 실제로는 2개
                        GuestHouseCreateRequest.RoomRequest.builder()
                                .name("Room 1")
                                .capacity(2)
                                .price(50000)
                                .build(),
                        GuestHouseCreateRequest.RoomRequest.builder()
                                .name("Room 2")
                                .capacity(4)
                                .price(80000)
                                .build()
                ))
                .build();

        // When & Then
        assertThatThrownBy(() -> guesthouseService.createGuestHouseWithRooms(1, invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("room_count mismatch with rooms array size");
    }

    @Test
    @DisplayName("내 게스트하우스 목록 조회")
    void getMyGuesthouses_Success() {
        // Given
        List<GuesthouseRepository.GuesthouseSummary> expectedList = Arrays.asList();
        when(guesthouseRepository.findMyGuesthouses(1)).thenReturn(expectedList);

        // When
        List<GuesthouseRepository.GuesthouseSummary> result = guesthouseService.getMyGuesthouses(1);

        // Then
        assertThat(result).isEqualTo(expectedList);
        verify(guesthouseRepository).findMyGuesthouses(1);
    }

    @Test
    @DisplayName("게스트하우스 삭제 성공")
    void deleteGuesthouse_Success() {
        // Given
        when(guesthouseRepository.findById(1)).thenReturn(Optional.of(testGuesthouse));

        // When
        guesthouseService.deleteGuesthouse(1, 1);

        // Then
        verify(guesthouseRepository).findById(1);
        verify(guesthouseRepository).delete(testGuesthouse);
    }

    @Test
    @DisplayName("존재하지 않는 게스트하우스 삭제 시 예외 발생")
    void deleteGuesthouse_GuesthouseNotFound() {
        // Given
        when(guesthouseRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> guesthouseService.deleteGuesthouse(999, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Guesthouse not found");
    }

    @Test
    @DisplayName("소유자가 아닌 사용자가 게스트하우스 삭제 시 예외 발생")
    void deleteGuesthouse_NotOwner() {
        // Given
        when(guesthouseRepository.findById(1)).thenReturn(Optional.of(testGuesthouse));

        // When & Then
        assertThatThrownBy(() -> guesthouseService.deleteGuesthouse(1, 999))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("You are not the owner of this guesthouse.");
    }

    @Test
    @DisplayName("게스트하우스의 예약 목록 조회 성공")
    void getReservationsByGuesthouse_Success() {
        // Given
        User guest = User.builder()
                .id(2)
                .username("testguest")
                .build();

        Reservation reservation = Reservation.builder()
                .id(1)
                .room(testRoom)
                .guest(guest)
                .checkInDate(LocalDate.of(2024, 12, 1))
                .checkOutDate(LocalDate.of(2024, 12, 3))
                .peopleCount(2)
                .build();

        when(guesthouseRepository.findById(1)).thenReturn(Optional.of(testGuesthouse));
        when(reservationRepository.findAllByRoom_Guesthouse_IdOrderByCheckInDateAsc(1))
                .thenReturn(Arrays.asList(reservation));

        // When
        List<ReservationListItemDto> result = guesthouseService.getReservationsByGuesthouse(1, 1);

        // Then
        assertThat(result).hasSize(1);
        ReservationListItemDto dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getRoomId()).isEqualTo(1);
        assertThat(dto.getRoomName()).isEqualTo("Test Room");
        assertThat(dto.getGuest().getId()).isEqualTo(2);
        assertThat(dto.getGuest().getUsername()).isEqualTo("testguest");
        assertThat(dto.getCheckInDate()).isEqualTo(LocalDate.of(2024, 12, 1));
        assertThat(dto.getCheckOutDate()).isEqualTo(LocalDate.of(2024, 12, 3));
        assertThat(dto.getPeopleCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("존재하지 않는 게스트하우스의 예약 조회 시 예외 발생")
    void getReservationsByGuesthouse_GuesthouseNotFound() {
        // Given
        when(guesthouseRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> guesthouseService.getReservationsByGuesthouse(999, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Guesthouse not found");
    }

    @Test
    @DisplayName("소유자가 아닌 사용자가 예약 목록 조회 시 예외 발생")
    void getReservationsByGuesthouse_NotOwner() {
        // Given
        when(guesthouseRepository.findById(1)).thenReturn(Optional.of(testGuesthouse));

        // When & Then
        assertThatThrownBy(() -> guesthouseService.getReservationsByGuesthouse(1, 999))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("You are not the owner of this guesthouse.");
    }

    @Test
    @DisplayName("예약이 없는 게스트하우스 조회")
    void getReservationsByGuesthouse_EmptyReservations() {
        // Given
        when(guesthouseRepository.findById(1)).thenReturn(Optional.of(testGuesthouse));
        when(reservationRepository.findAllByRoom_Guesthouse_IdOrderByCheckInDateAsc(1))
                .thenReturn(Arrays.asList());

        // When
        List<ReservationListItemDto> result = guesthouseService.getReservationsByGuesthouse(1, 1);

        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    public void testUpdateRating() {
        Integer guesthouseId = 1;
        Double expectedAverage = 4.5;

        // Repository의 calculateAverageRating이 실제로 값을 반환하도록 설정
        when(guesthouseRepository.calculateAverageRating(guesthouseId)).thenReturn(expectedAverage);

        // Service 메서드 호출
        guesthouseService.updateRating(guesthouseId);

        // Repository 메서드가 호출되었는지 검증
        verify(guesthouseRepository, times(1)).calculateAverageRating(guesthouseId);

        // 만약 updateRating이 내부적으로 값을 사용하여 Guesthouse 엔티티를 업데이트 한다면,
        // 해당 값도 검증 가능 (추가 로직 필요)
    }
}