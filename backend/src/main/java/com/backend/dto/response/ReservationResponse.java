package com.backend.dto.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationResponse {
    private Integer id;
    private Integer roomId;
    private Integer guesthouseId;
    private String guesthouseName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer peopleCount;
    private Integer reviewId;
    private String reviewComment;
}