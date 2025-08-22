package com.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.ReservationRequest;
import com.backend.dto.response.ReservationResponse;
import com.backend.repository.UserRepository;
import com.backend.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {
    private final UserController userController;
	private final ReservationService reservationService;
	
	@PostMapping
	public ResponseEntity createReservation(
			@RequestHeader("user-id") Integer userId, 
			@RequestBody ReservationRequest request) {
	    boolean success = reservationService.createReservation(userId, request);
	    if (success) {
	        return ResponseEntity.status(HttpStatus.CREATED).build();
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); 
	    }
	}
	
    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(
            @RequestHeader("user-id") Integer userId) {
        List<ReservationResponse> reservations = reservationService.getMyReservations(userId);
        return ResponseEntity.ok(reservations);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Map<String, Boolean>> cancelReservation(
            @PathVariable("reservationId") Integer reservationId,
            @RequestHeader("user-id") Integer userId) {
        boolean success = reservationService.cancelReservation(userId, reservationId);
        return ResponseEntity.ok(
        		Map.of("success", success)
        		);
    }
}
