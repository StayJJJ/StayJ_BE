package com.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.dto.response.GuesthouseResponseDto;
import com.backend.entity.Guesthouse;
import com.backend.entity.Room;
import com.backend.repository.GuesthouseRepository;

@ExtendWith(MockitoExtension.class)
public class GuesthouseListServiceTest {
    private GuesthouseRepository guesthouseRepository;
    private GuesthouseListService guesthouseListService;

    private Guesthouse guesthouse1;
    private Guesthouse guesthouse2;

    @BeforeEach
    void setUp() {
        guesthouseRepository = Mockito.mock(GuesthouseRepository.class);
        guesthouseListService = new GuesthouseListService(guesthouseRepository);

        // Room 생성
        Room room1 = Room.builder()
                .id(1)
                .name("Room A")
                .capacity(2)
                .price(100)
                .build();

        Room room2 = Room.builder()
                .id(2)
                .name("Room B")
                .capacity(4)
                .price(150)
                .build();

        // 가용성 메서드 모킹
        room1 = spy(room1);
        room2 = spy(room2);
        doReturn(true).when(room1).isAvailable(any(), any(), anyInt());
        doReturn(false).when(room2).isAvailable(any(), any(), anyInt());

        // Guesthouse 생성
        guesthouse1 = Guesthouse.builder()
                .id(1)
                .name("Sea View Guesthouse")
                .address("Beach Street")
                .rating(4.5)
                .photoId(101)
                .roomCount(2)
                .build();
        guesthouse1.addRoom(room1);
        guesthouse1.addRoom(room2);

        guesthouse2 = Guesthouse.builder()
                .id(2)
                .name("Mountain Inn")
                .address("Hill Road")
                .rating(4.0)
                .photoId(102)
                .roomCount(1)
                .build();
        guesthouse2.addRoom(room2);
    }

    @Test
    @DisplayName("모든 게스트하우스 조회 후 방 가용성 필터링")
    void searchGuesthouses_FilterAvailableRooms_Success() {
        // Given
        when(guesthouseRepository.findAll()).thenReturn(Arrays.asList(guesthouse1, guesthouse2));

        LocalDate checkIn = LocalDate.of(2025, 9, 1);
        LocalDate checkOut = LocalDate.of(2025, 9, 3);

        // When
        List<GuesthouseResponseDto> result = guesthouseListService.searchGuesthouses(
                1, checkIn, checkOut, "sea", 2);

        // Then
        assertThat(result).hasSize(1);
        GuesthouseResponseDto dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).containsIgnoringCase("sea");
        assertThat(dto.getRoomAvailable()).containsExactly(1); // room1만 가용
        assertThat(dto.getPhotoId()).isEqualTo(101);

        verify(guesthouseRepository).findAll();
    }
}