package com.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.ReservationRequest;
import com.backend.dto.response.ReservationResponse;
import com.backend.service.ReservationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
@Tag(name = "Reservation API", description = "예약 생성/조회/취소")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(
        summary = "예약 생성",
        description = "사용자 헤더(`user-id`)와 요청 바디를 받아 예약을 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "예약 생성 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 오류 또는 비즈니스 제약 위반",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\":\"BAD_REQUEST\"}")))
    })
    @PostMapping
    public ResponseEntity<Void> createReservation(
        @Parameter(
            name = "user-id", in = ParameterIn.HEADER, required = true,
            description = "요청 사용자 ID", example = "1",
            schema = @Schema(type = "integer", format = "int32")
        )
        @RequestHeader("user-id") Integer userId,

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "예약 생성 요청 바디",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReservationRequest.class),
                examples = @ExampleObject(
                    name = "reservation-create",
                    value = "{\n" +
                            "  \"room_id\": 7,\n" +
                            "  \"check_in_date\": \"2025-09-10\",\n" +
                            "  \"check_out_date\": \"2025-09-12\",\n" +
                            "  \"people_count\": 2\n" +
                            "}"
                )
            )
        )
        @RequestBody ReservationRequest request
    ) {
        boolean success = reservationService.createReservation(userId, request);
        return success ? ResponseEntity.status(HttpStatus.CREATED).build()
                       : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Operation(
        summary = "내 예약 목록 조회",
        description = "헤더의 `user-id` 기준으로 본인의 예약 목록을 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ReservationResponse.class)),
                examples = @ExampleObject(
                    name = "reservation-list",
                    value = "[\n" +
                            "  {\n" +
                            "    \"reservation_id\": 101,\n" +
                            "    \"guesthouse_name\": \"제주 힐링하우스\",\n" +
                            "    \"room_name\": \"오션뷰 룸\",\n" +
                            "    \"check_in\": \"2025-09-01\",\n" +
                            "    \"check_out\": \"2025-09-03\",\n" +
                            "    \"people\": 2,\n" +
                            "    \"status\": \"CONFIRMED\"\n" +
                            "  }\n" +
                            "]"
                )
            )
        )
    })
    
    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(
        @Parameter(
            name = "user-id", in = ParameterIn.HEADER, required = true,
            description = "요청 사용자 ID", example = "5",
            schema = @Schema(type = "integer", format = "int32")
        )
        @RequestHeader("user-id") Integer userId
    ) {
        List<ReservationResponse> reservations = reservationService.getMyReservations(userId);
        return ResponseEntity.ok(reservations);
    }

    @Operation(
        summary = "예약 취소",
        description = "예약 ID와 사용자 헤더(`user-id`)를 받아 예약을 취소합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "취소 결과",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "cancel-result",
                    value = "{ \"success\": true }"
                )
            )
        ),
        @ApiResponse(responseCode = "404", description = "예약 없음",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\":\"NOT_FOUND\"}")))
    })
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Map<String, Boolean>> cancelReservation(
        @Parameter(description = "예약 ID", required = true, example = "139")
        @PathVariable("reservationId") Integer reservationId,

        @Parameter(
            name = "user-id", in = ParameterIn.HEADER, required = true,
            description = "요청 사용자 ID", example = "10",
            schema = @Schema(type = "integer", format = "int32")
        )
        @RequestHeader("user-id") Integer userId
    ) {
        boolean success = reservationService.cancelReservation(userId, reservationId);
        return ResponseEntity.ok(Map.of("success", success));
    }
}
