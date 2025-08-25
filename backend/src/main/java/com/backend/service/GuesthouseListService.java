package com.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.dto.response.GuesthouseResponseDto;
import com.backend.entity.Guesthouse;
import com.backend.entity.Room;
import com.backend.repository.GuesthouseRepository;

@Service
public class GuesthouseListService {

    private final GuesthouseRepository guesthouseRepository;

    public GuesthouseListService(GuesthouseRepository guesthouseRepository) {
        this.guesthouseRepository = guesthouseRepository;
    }

    public List<GuesthouseResponseDto> searchGuesthouses(int userId, LocalDate checkIn, LocalDate checkOut, String name, int people) {
        List<Guesthouse> allGuesthouses = guesthouseRepository.findAll();

        return allGuesthouses.stream()
                .filter(guesthouse -> name == null || guesthouse.getName().toLowerCase().contains(name.toLowerCase()))
                .map(guesthouse -> {
                    // 각 게스트하우스의 예약 가능한 방 필터링
                    List<Integer> availableRoomIds = guesthouse.getRoomList().stream()
                            .filter(room -> room.isAvailable(checkIn, checkOut, people))
                            .map(Room::getId)
                            .collect(Collectors.toList());

                    return GuesthouseResponseDto.builder()
                            .id(guesthouse.getId())
                            .name(guesthouse.getName())
                            .address(guesthouse.getAddress())
                            .rating(guesthouse.getRating())
                            .photoId(guesthouse.getPhotoId()) // 여기 바꿔주기
                            .roomCount(guesthouse.getRoomCount())
                            .roomAvailable(availableRoomIds)
                            .build();
                })
                .filter(dto -> !dto.getRoomAvailable().isEmpty()) // 방이 하나도 없으면 제외
                .collect(Collectors.toList());
    }
}
