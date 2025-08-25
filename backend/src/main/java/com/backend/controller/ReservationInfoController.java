package com.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.backend.dto.request.GuestHouseDetailRequest;
import com.backend.dto.request.GuestHouseRoomRequest;
import com.backend.dto.request.RoomResponseRequest;
import com.backend.dto.response.ReviewResponse;
import com.backend.service.ReservationInfoService;
import jakarta.persistence.EntityNotFoundException;

@Controller
@ResponseBody
@RequestMapping("/guesthouse")
public class ReservationInfoController {

    @Autowired
    public ReservationInfoService ReservationInfoService;

    @GetMapping("/{guesthouse_Id}")
    public ResponseEntity<GuestHouseDetailRequest> getGuestHouseDetail(
            @PathVariable("guesthouse_Id") int guesthouseId) {

        GuestHouseDetailRequest response = ReservationInfoService.getGuestHouseDetail(guesthouseId);
        return ResponseEntity.ok(response);
    }
    
	@GetMapping("/{guesthouse_Id}/rooms")
	public ResponseEntity<RoomResponseRequest> getGuestHouseRoom(
	        @PathVariable("guesthouse_Id") int guesthouseId,
	        @RequestParam(value = "room_available", required = false) List<Integer> roomAvailable) {
	    
		RoomResponseRequest response = ReservationInfoService.getGuestHouseRooms(guesthouseId, roomAvailable);
	    return ResponseEntity.ok(response);
	}
    	
    @GetMapping("{guesthouse_Id}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReview(
    		@PathVariable("guesthouse_Id") int guesthouseId) {
    	
    	List<ReviewResponse> response = ReservationInfoService.getReview(guesthouseId);
    	return ResponseEntity.ok(response);
    }
    

}