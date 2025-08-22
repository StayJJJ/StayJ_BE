package com.backend.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GuestHouseCreateRequest {
	private String name;
	private String description;
	private String address;
	private Double rating;

	@JsonProperty("phone_number")
	private String phoneNumber;
	@JsonProperty("photo_id")
	private Integer photoId;
    @JsonProperty("room_count")
	private Integer roomCount;
    
	private List<RoomRequest> rooms;
}
