package com.backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.response.GuesthouseResponseDto;
import com.backend.service.GuesthouseListService;

@RestController
@RequestMapping("/guesthouse")
public class GuesthouseListController {
    private final GuesthouseListService guesthouseListService;

    public GuesthouseListController(GuesthouseListService guesthouseListService) {
        this.guesthouseListService = guesthouseListService;
    }

    @GetMapping("/search")
    public List<GuesthouseResponseDto> searchGuesthouses(
            @RequestParam(name = "check_in", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,

            @RequestParam(name = "check_out", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,

            @RequestParam(name = "name", required = false) String name,

            @RequestParam(name = "people", required = false) Integer people,
    ) {
        // 기본값 처리
        if (checkIn == null) {
            checkIn = LocalDate.now();
        }
        if (checkOut == null) {
            checkOut = checkIn.plusDays(1);
        }
        if (people == null) {
            people = 1;
        }

        int userId = userRequest != null ? userRequest.getUserId() : -1;

        return guesthouseListService.searchGuesthouses(userId, checkIn, checkOut, name, people);
    }
}
