package com.backend.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class GuestHouseCreateRequest {
    private String name;
    private String description;
    private String address;
    private Double rating;
    private String phoneNumber;
    private Integer photoId;
    private Integer roomCount;
    private Integer hostId;
    private List<RoomRequest> rooms;
}
