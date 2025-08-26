package com.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.GuestHouseDetailRequest;
import com.backend.dto.request.RoomResponseRequest;
import com.backend.dto.response.ReviewResponse;
import com.backend.service.ReservationInfoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@ResponseBody
@RequestMapping("/guesthouse")
@Tag(name = "Reservation Info API", description = "게스트하우스 상세/객실/리뷰 조회")
public class ReservationInfoController {

    @Autowired
    public ReservationInfoService reservationInfoService;

    // ---------------------------------------------------------
    // 1) 게스트하우스 상세 조회
    // ---------------------------------------------------------
    @Operation(
        summary = "게스트하우스 상세 조회",
        description = "게스트하우스의 기본 정보(소개, 주소, 연락처, 사진 등)를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = GuestHouseDetailRequest.class))),
            @ApiResponse(responseCode = "404", description = "해당 게스트하우스가 존재하지 않음", content = @Content)
        }
    )
    @GetMapping("/{guesthouse_Id}")
    public ResponseEntity<GuestHouseDetailRequest> getGuestHouseDetail(
        @Parameter(
            name = "guesthouse_Id",
            in = ParameterIn.PATH,
            required = true,
            description = "게스트하우스 ID",
            example = "10",
            schema = @Schema(type = "integer", format = "int32")
        )
        @PathVariable("guesthouse_Id") int guesthouseId
    ) {
        var response = reservationInfoService.getGuestHouseDetail(guesthouseId);
        return ResponseEntity.ok(response);
    }

    // ---------------------------------------------------------
    // 2) 게스트하우스 객실 목록 조회
    // ---------------------------------------------------------
    @Operation(
        summary = "게스트하우스 객실 목록 조회",
        description = "해당 게스트하우스의 객실 정보를 조회합니다. 선택적으로 `room_available`(객실 ID 배열)을 전달하면 해당 ID만 필터링하여 응답합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = RoomResponseRequest.class))),
            @ApiResponse(responseCode = "404", description = "해당 게스트하우스가 존재하지 않음", content = @Content)
        }
    )
    @GetMapping("/{guesthouse_Id}/rooms")
    public ResponseEntity<RoomResponseRequest> getGuestHouseRoom(
        @Parameter(
            name = "guesthouse_Id",
            in = ParameterIn.PATH,
            required = true,
            description = "게스트하우스 ID",
            example = "10",
            schema = @Schema(type = "integer", format = "int32")
        )
        @PathVariable("guesthouse_Id") int guesthouseId,

        @Parameter(
            name = "room_available",
            in = ParameterIn.QUERY,
            required = false,
            description = "가용한 객실 ID 배열 (예: room_available=1&room_available=2)",
            array = @ArraySchema(schema = @Schema(type = "integer", format = "int32"))
        )
        @RequestParam(value = "room_available", required = false) List<Integer> roomAvailable
    ) {
        var response = reservationInfoService.getGuestHouseRooms(guesthouseId, roomAvailable);
        return ResponseEntity.ok(response);
    }

    // ---------------------------------------------------------
    // 3) 게스트하우스 리뷰 목록 조회
    // ---------------------------------------------------------
    @Operation(
        summary = "게스트하우스 리뷰 목록 조회",
        description = "해당 게스트하우스에 등록된 리뷰 목록을 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewResponse.class)))),
            @ApiResponse(responseCode = "404", description = "해당 게스트하우스가 존재하지 않음", content = @Content)
        }
    )
    @GetMapping("{guesthouse_Id}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReview(
        @Parameter(
            name = "guesthouse_Id",
            in = ParameterIn.PATH,
            required = true,
            description = "게스트하우스 ID",
            example = "10",
            schema = @Schema(type = "integer", format = "int32")
        )
        @PathVariable("guesthouse_Id") int guesthouseId
    ) {
        var response = reservationInfoService.getReview(guesthouseId);
        return ResponseEntity.ok(response);
    }
}
