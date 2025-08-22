package com.backend.dto.request;

import lombok.Data;

@Data
public class RoomRequest {
    private String name;
    private Integer capacity;
    private Integer price;
    private Integer photoId;
}
