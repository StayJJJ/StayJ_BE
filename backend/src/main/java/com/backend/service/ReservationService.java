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

import jakarta.transaction.Transactional;
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
	
	@Transactional
	public boolean createReservation(Integer userId, ReservationRequest request) {
        // 0) 기초 검증
        if (request.getPeopleCount() == null || request.getPeopleCount() <= 0) return false;
        if (request.getCheckInDate() == null || request.getCheckOutDate() == null) return false;
        if (!request.getCheckInDate().isBefore(request.getCheckOutDate())) return false;

        // 1) 엔티티 조회
        User guest = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // (선택) 방 레코드에 잠금 걸기: RoomRepository에 잠금 메서드 추가 권장 (아래 주석 참고)
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // 2) 현재 기간에 이미 예약된 인원 합계 조회
        int alreadyReserved = reservationRepository.sumPeopleCountForOverlap(
                room.getId(), request.getCheckInDate(), request.getCheckOutDate());

        int remaining = room.getCapacity() - alreadyReserved;

        // 3) 현재 요청 인원 수용 가능 여부 확인
        if (request.getPeopleCount() > remaining) {
            return false; // 정원 초과
        }

        // 4) 저장
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
	                .reviewId(r.getReview() != null ? r.getReview().getId() : null)
	                .reviewComment(r.getReview() != null ? r.getReview().getComment() : null)
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
