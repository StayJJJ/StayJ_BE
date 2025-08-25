package com.backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.dto.request.ReservationRequest;
import com.backend.dto.response.ReservationResponse;
import com.backend.entity.Reservation;
import com.backend.entity.Room;
import com.backend.entity.User;
import com.backend.repository.ReservationRepository;
import com.backend.repository.RoomRepository;
import com.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	@Autowired
	private final UserRepository userRepository;
	
	@Autowired
	private final ReservationRepository reservationRepository;
	
	@Autowired
	private final RoomRepository roomRepository;
	
	public boolean createReservation(Integer userId, ReservationRequest request) {
		// 1. User, Room 조회
        User guest = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // 2. 중복 예약 확인
        List<Reservation> reservations = reservationRepository.findByRoomId(room.getId());
        for (Reservation r : reservations) {
            if (r.isOverlapping(request.getCheckInDate(), request.getCheckOutDate())) {
                return false; // 중복 예약 있음
            }
        }

        // 3. 인원 수 체크
        if (request.getPeopleCount() > room.getCapacity()) {
            return false; // 인원 초과
        }

        // 4. 예약 저장
        Reservation reservation = Reservation.builder()
                .guest(guest)
                .room(room)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .peopleCount(request.getPeopleCount())
                .build();

        reservationRepository.save(reservation);
        return true;
	}
	
	public List<ReservationResponse> getMyReservations(Integer guestId) {
	    List<Reservation> reservations = reservationRepository.findByGuest_Id(guestId);
	    List<ReservationResponse> responses = new ArrayList<>();

	    for (Reservation r : reservations) {
	        ReservationResponse response = ReservationResponse.builder()
	                .id(r.getId())
	                .roomId(r.getRoom().getId())
	                .guesthouseId(r.getRoom().getGuesthouse().getId())
	                .guesthouseName(r.getRoom().getGuesthouse().getName())
	                .checkInDate(r.getCheckInDate())
	                .checkOutDate(r.getCheckOutDate())
	                .peopleCount(r.getPeopleCount())
	                .reviewed(r.getReview() != null)
	                .build();

	        responses.add(response);
	    }

	    return responses;
	}

    public boolean cancelReservation(Integer userId, Integer reservationId) {
        return reservationRepository.findById(reservationId)
        		.filter(r -> r.getGuest().getId().equals(userId))
                .map(r -> {
                    if (LocalDate.now().isBefore(r.getCheckInDate())) {
                        reservationRepository.delete(r);
                        return true;
                    } else {
                        return false;
                    }
                })
                .orElse(false);
    }
}
