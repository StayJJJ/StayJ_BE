package com.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GuesthouseListItemDto {
    private Integer id;
    private String name;
    
    @JsonProperty("room_count")
    private Integer roomCount; // 또는 roomCount
    private Double rating;
}
