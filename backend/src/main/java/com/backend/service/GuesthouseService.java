package com.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.backend.dto.request.GuestHouseCreateRequest;
import com.backend.dto.response.ReservationListItemDto;
import com.backend.dto.response.ReservationListItemDto.GuestSimpleDto;
import com.backend.entity.Guesthouse;
import com.backend.entity.Reservation;
import com.backend.entity.Room;
import com.backend.entity.User;
import com.backend.repository.GuesthouseRepository;
import com.backend.repository.ReservationRepository;
import com.backend.repository.RoomRepository;
import com.backend.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class GuesthouseService {
	@Autowired
	public UserRepository userRepository;

	@Autowired
	public GuesthouseRepository guesthouseRepository;

	@Autowired
	public RoomRepository roomRepository;

	@Autowired
	public ReservationRepository reservationRepository;

	@Transactional
	public Integer createGuestHouseWithRooms(Integer hostId, GuestHouseCreateRequest request) {
		// 1) 호스트 존재 검증
		User host = userRepository.findById(hostId).orElseThrow(() -> new IllegalArgumentException("Host not found"));

		if (request.getRooms() != null && request.getRoomCount() != null
                && !request.getRoomCount().equals(request.getRooms().size())) {
            throw new IllegalArgumentException("room_count mismatch with rooms array size");
        }
		
		// 2) Guesthouse 생성
		Guesthouse guesthouse = Guesthouse.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .rating(request.getRating())
                .photoId(request.getPhotoId())
                .phoneNumber(request.getPhoneNumber())
                .roomCount(request.getRoomCount())
                .host(host)
                .build();

		// 3) Rooms 추가 (guesthouse.addRoom로 양방향/주인세팅)
        if (request.getRooms() != null) {
            for (GuestHouseCreateRequest.RoomRequest r : request.getRooms()) {
                Room room = Room.builder()
                        .name(r.getName())
                        .capacity(r.getCapacity())
                        .price(r.getPrice())
                        .photoId(r.getPhotoId())
                        .build();
                guesthouse.addRoom(room);
            }
        }

        // 4) 저장 (CascadeType.ALL로 room까지 함께 저장)
        Guesthouse saved = guesthouseRepository.save(guesthouse);
        return saved.getId();
	}

	public List<GuesthouseRepository.GuesthouseSummary> getMyGuesthouses(Integer hostId) {
		return guesthouseRepository.findMyGuesthouses(hostId);
	}

	@Transactional
	public void deleteGuesthouse(Integer guesthouseId, Integer hostId) {
		Guesthouse guesthouse = guesthouseRepository.findById(guesthouseId)
				.orElseThrow(() -> new IllegalArgumentException("Guesthouse not found"));

		if (!guesthouse.getHost().getId().equals(hostId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this guesthouse.");
		}

		guesthouseRepository.delete(guesthouse);
	}
	
	@Transactional
    public List<ReservationListItemDto> getReservationsByGuesthouse(Integer guesthouseId, Integer hostId) {
        // 1) 존재/소유 검증
        Guesthouse guesthouse = guesthouseRepository.findById(guesthouseId)
            .orElseThrow(() -> new IllegalArgumentException("Guesthouse not found"));

        if (!guesthouse.getHost().getId().equals(hostId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this guesthouse.");
        }

        // 2) 예약 조회 (연관 로딩으로 N+1 방지)
        List<Reservation> reservations =
            reservationRepository.findAllByRoom_Guesthouse_IdOrderByCheckInDateAsc(guesthouseId);

        // 3) DTO 매핑
        return reservations.stream().map(r -> ReservationListItemDto.builder()
                .id(r.getId())
                .roomId(r.getRoom().getId())
                .roomName(r.getRoom().getName())
                .guest(GuestSimpleDto.builder()
                        .id(r.getGuest().getId())
                        .username(r.getGuest().getUsername())
                        .build())
                .checkInDate(r.getCheckInDate())
                .checkOutDate(r.getCheckOutDate())
                .peopleCount(r.getPeopleCount())
                .build()
        ).toList();
    }

}
