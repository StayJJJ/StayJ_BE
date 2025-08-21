package com.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateRequest {
    private Integer reservationId;
    private Integer rating;
    private String comment;
}

