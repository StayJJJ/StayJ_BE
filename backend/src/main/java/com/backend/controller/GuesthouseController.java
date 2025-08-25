package com.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.GuestHouseCreateRequest;
import com.backend.dto.request.GuesthouseListItemDto;
import com.backend.dto.response.ReservationListItemDto;
import com.backend.dto.response.SuccessResponse;
import com.backend.service.GuesthouseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/guesthouse")
@Tag(name = "Guesthouse API", description = "게스트하우스 관리 API")
public class GuesthouseController {

    @Autowired
    private GuesthouseService guesthouseService;

    @Operation(summary = "게스트하우스 생성", description = "새로운 게스트하우스를 등록합니다.")
    @PostMapping
    public ResponseEntity<SuccessResponse> createGuesthouse(
            @Parameter(name = "user-id", in = ParameterIn.HEADER, required = true, description = "호스트 사용자 ID")
            @RequestHeader("user-id") Integer hostId,
            @Valid @RequestBody GuestHouseCreateRequest request
    ) {
        Integer newId = guesthouseService.createGuestHouseWithRooms(hostId, request);
        return ResponseEntity.ok(new SuccessResponse(true));
    }

    @Operation(summary = "내 게스트하우스 목록 조회")
    @GetMapping("/mylist")
    public ResponseEntity<List<GuesthouseListItemDto>> getMyList(
            @Parameter(name = "user-id", in = ParameterIn.HEADER, required = true)
            @RequestHeader("user-id") Integer hostId
    ) {
        var rows = guesthouseService.getMyGuesthouses(hostId).stream()
                .map(p -> new GuesthouseListItemDto(p.getId(), p.getName(), p.getRoomCount(), p.getRating()))
                .toList();
        return ResponseEntity.ok(rows);
    }

    @Operation(summary = "게스트하우스 삭제")
    @DeleteMapping("/{guesthouseId}")
    public ResponseEntity<SuccessResponse> deleteGuesthouse(
            @Parameter(description = "게스트하우스 ID", required = true)
            @PathVariable("guesthouseId") Integer guesthouseId,
            @Parameter(name = "user-id", in = ParameterIn.HEADER, required = true)
            @RequestHeader("user-id") Integer hostId
    ) {
        guesthouseService.deleteGuesthouse(guesthouseId, hostId);
        return ResponseEntity.ok(new SuccessResponse(true));
    }

    @Operation(summary = "게스트하우스 예약 목록 조회")
    @GetMapping("/{guesthouseId}/reservations")
    public ResponseEntity<List<ReservationListItemDto>> getReservationsByGuesthouse(
            @Parameter(description = "게스트하우스 ID", required = true)
            @PathVariable("guesthouseId") Integer guesthouseId,
            @Parameter(name = "user-id", in = ParameterIn.HEADER, required = true)
            @RequestHeader("user-id") Integer hostId
    ) {
        var list = guesthouseService.getReservationsByGuesthouse(guesthouseId, hostId);
        return ResponseEntity.ok(list);
    }
}
