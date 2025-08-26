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

import com.backend.dto.request.ReviewCreateRequest;
import com.backend.dto.request.ReviewUpdateRequest;
import com.backend.dto.response.ReviewResponseDto;
import com.backend.service.ReviewService;

@RestController
@RequestMapping("/review")
public class ReviewController {
	private final ReviewService reviewService;
	
	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}
	
	// 리뷰 작성
	@PostMapping
	public ResponseEntity<?> createReview(
			@RequestHeader("user-id") Integer userId,
			@RequestBody ReviewCreateRequest request
	){
		System.out.println(request.reservationId);
		ReviewResponseDto response = reviewService.createReview(userId, request);
		return ResponseEntity.ok(response);
	}
	
	// 리뷰 수정
	@PatchMapping("/{reviewId}")
	public ResponseEntity<?> updateReview(
			@RequestHeader("user-id") Integer userId,
			@PathVariable("reviewId") int reviewId,
			@RequestBody ReviewUpdateRequest request
	){
		boolean success = reviewService.updateReview(userId,reviewId,request);
		if (success) {
			return ResponseEntity.ok(Map.of("success",true));
		} else {
			return ResponseEntity.status(403).body("본인만 수정 가능합니다.");
		}
	}
	
	// 리뷰 삭제
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<?> deleteReview(
            @RequestHeader("user-id") Integer userId,
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
