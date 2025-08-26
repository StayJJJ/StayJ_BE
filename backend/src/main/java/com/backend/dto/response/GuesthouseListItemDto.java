package com.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class GuesthouseListItemDto {
    private Integer id;
    private String name;
    
    @JsonProperty("room_count")
    private Integer roomCount; // 또는 roomCount
    private Double rating;
    private Integer photoId;
}
