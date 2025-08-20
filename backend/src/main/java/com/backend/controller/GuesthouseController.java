package com.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.backend.dto.request.GuestHouseCreateRequest;
import com.backend.service.GuesthouseService;

@Controller
@ResponseBody
@RequestMapping("/guesthouse")
public class GuesthouseController {
	@Autowired
	public GuesthouseService guesthouseService;
	
	@PostMapping
	public ResponseEntity createGuesthouse(@RequestBody GuestHouseCreateRequest request) {
		guesthouseService.createGuestHouseWithRooms(request);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.build();
	}
}
