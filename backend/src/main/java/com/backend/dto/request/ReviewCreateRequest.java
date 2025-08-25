package com.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ReviewCreateRequest {
	@JsonProperty("reservationId")
    public Integer reservationId;
    
    @JsonProperty("rating")
    public Integer rating;
    
    @JsonProperty("comment")
    public String comment;
}

