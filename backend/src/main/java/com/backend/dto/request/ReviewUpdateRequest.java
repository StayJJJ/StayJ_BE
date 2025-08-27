package com.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewUpdateRequest {
	private Integer rating;
	private String comment;
}
