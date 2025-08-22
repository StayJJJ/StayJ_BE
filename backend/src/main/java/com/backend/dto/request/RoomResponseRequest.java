package com.backend.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Builder
@Getter
public class RoomResponseRequest {
	private List<GuestHouseRoomRequest> rooms;
}
