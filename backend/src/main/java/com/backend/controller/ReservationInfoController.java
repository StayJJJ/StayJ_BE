package com.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.backend.dto.request.GuestHouseDetailRequest;
import com.backend.service.ReservationInfoService;
import jakarta.persistence.EntityNotFoundException;

@Controller
@ResponseBody
@RequestMapping("/guesthouse")
public class ReservationInfoController {

    @Autowired
    public ReservationInfoService reservationInfoService;

    @GetMapping("/{guesthouse_Id}")
    public ResponseEntity<GuestHouseDetailRequest> getGuestHouseDetail(
            @PathVariable("guesthouse_Id") int guesthouseId) {
    	System.out.println(guesthouseId);
    	

        GuestHouseDetailRequest response = reservationInfoService.getGuestHouseDetail(guesthouseId);
        return ResponseEntity.ok(response);
    }
}