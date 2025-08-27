package com.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.GuestHouseCreateRequest;
import com.backend.dto.response.GuesthouseListItemDto;
import com.backend.dto.response.ReservationListItemDto;
import com.backend.dto.response.SuccessResponse;
import com.backend.service.GuesthouseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/guesthouse")
@Tag(name = "Guesthouse API", description = "게스트하우스 관리 API")
@RequiredArgsConstructor
public class GuesthouseController {

	private final GuesthouseService guesthouseService;

	// ---------------------------------------------------------
	// 1) 게스트하우스 생성
	// ---------------------------------------------------------
	@Operation(summary = "게스트하우스 생성", description = "호스트가 새로운 게스트하우스를 등록합니다. 방 정보까지 함께 생성할 수 있습니다.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "게스트하우스 생성 요청", required = true, content = @Content(schema = @Schema(implementation = GuestHouseCreateRequest.class), examples = @ExampleObject(name = "게스트하우스 생성 예시", value = """
			{
			  "name": "제주 바다안개 하우스",
			  "description": "바다 전망, 조용한 숙소",
			  "address": "제주시 애월읍 1-1",
			  "rating": 0.0,
			  "photo_id": 1,
			  "phone_number": "064-700-0001",
			  "room_count": 1,
			  "rooms": [
			    {
			      "name": "스탠다드",
			      "capacity": 2,
			      "price": 52000,
			      "photo_id": 1
			    }
			  ]
			}
			"""))), responses = {
			@ApiResponse(responseCode = "200", description = "생성 성공", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
			@ApiResponse(responseCode = "400", description = "유효하지 않은 요청 본문", content = @Content),
			@ApiResponse(responseCode = "403", description = "호스트 권한 없음", content = @Content) })
	@PostMapping
	public ResponseEntity<SuccessResponse> createGuesthouse(
			@Parameter(name = "user-id", in = ParameterIn.HEADER, required = true, description = "호스트 사용자 ID", example = "1", schema = @Schema(type = "integer", format = "int32")) @RequestHeader("user-id") Integer hostId,
			@Valid @RequestBody GuestHouseCreateRequest request) {
		Integer newId = guesthouseService.createGuestHouseWithRooms(hostId, request);
		return ResponseEntity.ok(new SuccessResponse(true));
	}

	// ---------------------------------------------------------
	// 2) 내 게스트하우스 목록 조회
	// ---------------------------------------------------------
	@Operation(summary = "내 게스트하우스 목록 조회", description = "헤더의 user-id(호스트)로 소유한 게스트하우스 목록을 조회합니다.", responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = GuesthouseListItemDto.class))),
			@ApiResponse(responseCode = "403", description = "권한 없음(호스트가 아님)", content = @Content) })
	@GetMapping("/mylist")
	public ResponseEntity<List<GuesthouseListItemDto>> getMyList(
			@Parameter(name = "user-id", in = ParameterIn.HEADER, required = true, description = "호스트 사용자 ID", example = "1", schema = @Schema(type = "integer", format = "int32")) @RequestHeader("user-id") Integer hostId) {
		var rows = guesthouseService.getMyGuesthouses(hostId).stream().map(
				p -> new GuesthouseListItemDto(p.getId(), p.getName(), p.getRoomCount(), p.getRating(), p.getPhotoId()))
				.toList();
		return ResponseEntity.ok(rows);
	}

	// ---------------------------------------------------------
	// 3) 게스트하우스 삭제
	// ---------------------------------------------------------
	@Operation(summary = "게스트하우스 삭제", description = "특정 게스트하우스를 삭제합니다. 본인 소유의 게스트하우스만 삭제할 수 있습니다.", responses = {
			@ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
			@ApiResponse(responseCode = "403", description = "소유자가 아님 / 권한 없음", content = @Content),
			@ApiResponse(responseCode = "404", description = "게스트하우스가 존재하지 않음", content = @Content) })
	@DeleteMapping("/{guesthouseId}")
	public ResponseEntity<SuccessResponse> deleteGuesthouse(
			@Parameter(name = "guesthouseId", description = "게스트하우스 ID", required = true, example = "1", schema = @Schema(type = "integer", format = "int32")) @PathVariable("guesthouseId") Integer guesthouseId,

			@Parameter(name = "user-id", in = ParameterIn.HEADER, required = true, description = "호스트 사용자 ID", example = "1", schema = @Schema(type = "integer", format = "int32")) @RequestHeader("user-id") Integer hostId) {
		guesthouseService.deleteGuesthouse(guesthouseId, hostId);
		return ResponseEntity.ok(new SuccessResponse(true));
	}

	// ---------------------------------------------------------
	// 4) 게스트하우스 예약 목록 조회
	// ---------------------------------------------------------
	@Operation(summary = "게스트하우스 예약 목록 조회", description = "특정 게스트하우스에 대한 예약 목록을 조회합니다. 호스트 본인의 게스트하우스만 조회할 수 있습니다.", responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ReservationListItemDto.class))),
			@ApiResponse(responseCode = "403", description = "소유자가 아님 / 권한 없음", content = @Content),
			@ApiResponse(responseCode = "404", description = "게스트하우스가 존재하지 않음", content = @Content) })
	@GetMapping("/{guesthouseId}/reservations")
	public ResponseEntity<List<ReservationListItemDto>> getReservationsByGuesthouse(
			@Parameter(name = "guesthouseId", description = "게스트하우스 ID", required = true, example = "1", schema = @Schema(type = "integer", format = "int32")) @PathVariable("guesthouseId") Integer guesthouseId,

			@Parameter(name = "user-id", in = ParameterIn.HEADER, required = true, description = "호스트 사용자 ID", example = "1", schema = @Schema(type = "integer", format = "int32")) @RequestHeader("user-id") Integer hostId) {
		var list = guesthouseService.getReservationsByGuesthouse(guesthouseId, hostId);
		return ResponseEntity.ok(list);
	}
}
