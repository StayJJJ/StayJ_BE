package com.backend.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponseDto {
    private Integer id;
    private Integer reservationId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
