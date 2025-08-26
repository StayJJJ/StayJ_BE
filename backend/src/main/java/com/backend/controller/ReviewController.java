package com.backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.ReviewCreateRequest;
import com.backend.dto.request.ReviewUpdateRequest;
import com.backend.dto.response.ReviewResponseDto;
import com.backend.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/review")
@Tag(name = "Review API", description = "리뷰 작성/수정/삭제 API")
public class ReviewController {
	private final ReviewService reviewService;
	
	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

    // ------------------------------------------------------------------
    // 1) 리뷰 작성
    // ------------------------------------------------------------------
    @Operation(
        summary = "리뷰 작성",
        description = "예약에 대한 리뷰를 작성합니다. 한 예약당 1개의 리뷰만 작성할 수 있습니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "리뷰 작성 요청 본문",
            required = true,
            content = @Content(
                schema = @Schema(implementation = ReviewCreateRequest.class),
                examples = @ExampleObject(
                    name = "리뷰 작성 예시",
                    value = """
                    {
                      "reservationId": 123,
                      "rating": 5,
                      "content": "방이 깨끗하고 호스트가 친절했어요!"
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "작성 성공",
                content = @Content(schema = @Schema(implementation = ReviewResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청(필드 오류/중복 작성 등)", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음(본인 예약이 아님 등)", content = @Content),
            @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음", content = @Content)
        }
    )
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(
        @Parameter(
            name = "user-id",
            in = ParameterIn.HEADER,
            required = true,
            description = "요청 사용자 ID",
            example = "1",
            schema = @Schema(type = "integer", format = "int32")
        )
        @RequestHeader("user-id") Integer userId,

        @Valid @org.springframework.web.bind.annotation.RequestBody ReviewCreateRequest request
    ){
        System.out.println(request);
        ReviewResponseDto response = reviewService.createReview(userId, request);
        return ResponseEntity.ok(response);
    }

    // ------------------------------------------------------------------
    // 2) 리뷰 수정
    // ------------------------------------------------------------------
    @Operation(
        summary = "리뷰 수정",
        description = "본인이 작성한 리뷰의 평점/내용을 수정합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "리뷰 수정 요청 본문(부분 수정 가능)",
            required = true,
            content = @Content(
                schema = @Schema(implementation = ReviewUpdateRequest.class),
                examples = @ExampleObject(
                    name = "리뷰 수정 예시",
                    value = """
                    {
                      "rating": 4,
                      "content": "전반적으로 만족했지만 소음이 조금 있었어요."
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음(본인 리뷰가 아님)", content = @Content),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음", content = @Content)
        }
    )
    @PatchMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
        @Parameter(
            name = "user-id",
            in = ParameterIn.HEADER,
            required = true,
            description = "요청 사용자 ID",
            example = "1",
            schema = @Schema(type = "integer", format = "int32")
        )
        @RequestHeader("user-id") Integer userId,

        @Parameter(
            name = "reviewId",
            in = ParameterIn.PATH,
            required = true,
            description = "수정할 리뷰 ID",
            example = "42",
            schema = @Schema(type = "integer", format = "int32")
        )
        @PathVariable("reviewId") int reviewId,

        @Valid @org.springframework.web.bind.annotation.RequestBody ReviewUpdateRequest request
    ){
        boolean success = reviewService.updateReview(userId, reviewId, request);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.status(403).body("본인만 수정 가능합니다.");
        }
    }

    // ------------------------------------------------------------------
    // 3) 리뷰 삭제
    // ------------------------------------------------------------------
    @Operation(
        summary = "리뷰 삭제",
        description = "본인이 작성한 리뷰를 삭제합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음(본인 리뷰가 아님)", content = @Content),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음", content = @Content)
        }
    )
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
        @Parameter(
            name = "user-id",
            in = ParameterIn.HEADER,
            required = true,
            description = "요청 사용자 ID",
            example = "1",
            schema = @Schema(type = "integer", format = "int32")
        )
        @RequestHeader("user-id") Integer userId,

        @Parameter(
            name = "reviewId",
            in = ParameterIn.PATH,
            required = true,
            description = "삭제할 리뷰 ID",
            example = "42",
            schema = @Schema(type = "integer", format = "int32")
        )
        @PathVariable("reviewId") int reviewId
    ) {
        boolean success = reviewService.deleteReview(userId, reviewId);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.status(403).body("본인만 삭제 가능합니다.");
        }
    }
	
    // 리뷰 단건 조회
	@GetMapping("/{reviewId}")
	public ResponseEntity<?> getReview(
			@RequestHeader("user-id") Integer userId, 
			@PathVariable("reviewId") int reviewId
			) {
	    ReviewResponseDto response = reviewService.getReviewById(reviewId);
	    if (response != null) {
	        return ResponseEntity.ok(response);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
}
