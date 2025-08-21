package com.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.GuestHouseCreateRequest;
import com.backend.dto.request.GuesthouseListItemDto;
import com.backend.service.GuesthouseService;

@RestController
@RequestMapping("/guesthouse")
public class GuesthouseController {
	@Autowired
	public GuesthouseService guesthouseService;

	@PostMapping
	public ResponseEntity<Void> createGuesthouse(
			@RequestHeader("user-id") Long hostId,
			@RequestBody GuestHouseCreateRequest request) {
		Long newId = guesthouseService.createGuestHouseWithRooms(hostId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/guesthouse/" + newId) // 선택
                .build();
	}

	/**
	 * GET /guesthouse/mylist 
	 * Header: user-id: <Long>
	 */
	@GetMapping("/mylist")
	public ResponseEntity<List<GuesthouseListItemDto>> getMyList(
			@RequestHeader("user-id") Long hostId) {
		var rows = guesthouseService.getMyGuesthouses(hostId).stream()
				.map(p -> new GuesthouseListItemDto(
						p.getId(), p.getName(), p.getRoomCount(), p.getRating())).toList();

		return ResponseEntity.ok(rows);
	}
}
