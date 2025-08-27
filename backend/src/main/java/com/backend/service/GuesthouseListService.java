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
                    // 예약 가능한 방 필터링
                    List<Room> availableRooms = guesthouse.getRoomList().stream()
                            .filter(room -> room.isAvailable(checkIn, checkOut, people))
                            .toList();

                    // 방 id 리스트
                    List<Integer> availableRoomIds = availableRooms.stream()
                            .map(Room::getId)
                            .toList();

                    // 최저가 계산 (예약 가능한 방이 있을 때만)
                    Integer minPrice = availableRooms.stream()
                            .map(Room::getPrice)
                            .min(Integer::compareTo)
                            .orElse(null);

                    return GuesthouseResponseDto.builder()
                            .id(guesthouse.getId())
                            .name(guesthouse.getName())
                            .address(guesthouse.getAddress())
                            .rating(guesthouse.getRating())
                            .photoId(guesthouse.getPhotoId())
                            .roomCount(guesthouse.getRoomCount())
                            .roomAvailable(availableRoomIds)
                            .minPrice(minPrice)
                            .build();
                })
                .filter(dto -> !dto.getRoomAvailable().isEmpty()) // 방이 하나도 없으면 제외
                .collect(Collectors.toList());
    }
}
