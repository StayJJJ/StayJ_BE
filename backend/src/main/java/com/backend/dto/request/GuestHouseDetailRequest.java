package com.backend.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuestHouseDetailRequest {
    private int id;
    private String name;
    private String description;
    private String address;
    private double rating;
    private int photoId;
    private int room_count;
}
