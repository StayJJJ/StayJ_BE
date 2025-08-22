package com.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.dto.request.ReservationRequest;
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
}
