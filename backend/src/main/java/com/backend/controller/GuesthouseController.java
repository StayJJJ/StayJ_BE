package com.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.GuestHouseCreateRequest;
import com.backend.dto.request.GuesthouseListItemDto;
import com.backend.dto.response.SuccessResponse;
import com.backend.dto.response.ReservationListItemDto;
import com.backend.service.GuesthouseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/guesthouse")
@Tag(name = "Guesthouse API", description = "게스트하우스 관리 API")
@RequiredArgsConstructor
public class GuesthouseController {
	@Autowired
    private final GuesthouseService guesthouseService;

    @Operation(summary = "게스트하우스 생성")
    @PostMapping
    public ResponseEntity<SuccessResponse> createGuesthouse(
            @RequestHeader("user-id") Integer hostId,
            @Valid @RequestBody GuestHouseCreateRequest request) {
        Integer newId = guesthouseService.createGuestHouseWithRooms(hostId, request);
        return ResponseEntity.ok(new SuccessResponse(true));
    }

    @Operation(summary = "내 게스트하우스 목록 조회")
    @GetMapping("/mylist")
    public ResponseEntity<List<GuesthouseListItemDto>> getMyList(
            @RequestHeader("user-id") Integer hostId) {
        var rows = guesthouseService.getMyGuesthouses(hostId).stream()
                .map(p -> new GuesthouseListItemDto(p.getId(), p.getName(), p.getRoomCount(), p.getRating()))
                .toList();
        return ResponseEntity.ok(rows);
    }

    @Operation(summary = "게스트하우스 삭제")
    @DeleteMapping("/{guesthouseId}")
    public ResponseEntity<SuccessResponse> deleteGuesthouse(
            @PathVariable("guesthouseId") Integer guesthouseId,
            @RequestHeader("user-id") Integer hostId) {
        guesthouseService.deleteGuesthouse(guesthouseId, hostId);
        return ResponseEntity.ok(new SuccessResponse(true));
    }

    @Operation(summary = "게스트하우스 예약 목록 조회")
    @GetMapping("/{guesthouseId}/reservations")
    public ResponseEntity<List<ReservationListItemDto>> getReservationsByGuesthouse(
            @PathVariable("guesthouseId") Integer guesthouseId,
            @RequestHeader("user-id") Integer hostId) {
        var list = guesthouseService.getReservationsByGuesthouse(guesthouseId, hostId);
        return ResponseEntity.ok(list);
    }
}
