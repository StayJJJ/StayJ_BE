package com.backend.controller;

import java.util.List;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.GuestHouseCreateRequest;
import com.backend.dto.request.GuesthouseListItemDto;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.ReservationListItemDto;
import com.backend.service.GuesthouseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/guesthouse")
public class GuesthouseController {
	@Autowired
	public GuesthouseService guesthouseService;


	@PostMapping
	public ResponseEntity<Void> createGuesthouse(
			@RequestHeader("user-id") Integer hostId,
			@RequestBody GuestHouseCreateRequest request) {
		Integer newId = guesthouseService.createGuestHouseWithRooms(hostId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/guesthouse/" + newId) // 선택
                .build();
	}

	/**
	 * GET /guesthouse/mylist 
	 * Header: user-id: <Integer>
	 */
	@GetMapping("/mylist")
	public ResponseEntity<List<GuesthouseListItemDto>> getMyList(
			@RequestHeader("user-id") Integer hostId) {
		var rows = guesthouseService.getMyGuesthouses(hostId).stream()
				.map(p -> new GuesthouseListItemDto(
						p.getId(), p.getName(), p.getRoomCount(), p.getRating())).toList();

		return ResponseEntity.ok(rows);
	}
	
	@DeleteMapping("/{guesthouse-id}")
	public ResponseEntity<ApiResponse> deleteGuesthouse(
			@PathVariable("guesthouse-id") Integer guesthouseId,
			@RequestHeader("user-id") Integer hostId) {
		guesthouseService.deleteGuesthouse(guesthouseId, hostId);
		return ResponseEntity.ok(new ApiResponse(true));
	}
	
	@GetMapping("/{guesthouse-id}/reservations")
	public ResponseEntity<List<ReservationListItemDto>> getReservationsByGuesthouse(
	        @PathVariable("guesthouse-id") Integer guesthouseId,
	        @RequestHeader("user-id") Integer hostId
	) {
	    var list = guesthouseService.getReservationsByGuesthouse(guesthouseId, hostId);
	    return ResponseEntity.ok(list);
	}
		
}
