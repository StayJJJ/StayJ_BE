package com.backend.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuestHouseRoomRequest {
	private int id;
    private String name;
    private int capacity;
    private int price;
}
