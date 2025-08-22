package com.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
	private int id;
    private int reservation_id;
    private int rating;
    private String comment;
    private LocalDateTime created_at;
}
