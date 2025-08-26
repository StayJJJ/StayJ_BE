package com.backend.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
@Data
@AllArgsConstructor
public class ReviewResponseDto {
    private Integer id;
    private Integer reservationId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
