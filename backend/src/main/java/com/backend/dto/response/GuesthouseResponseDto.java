package com.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuesthouseResponseDto {
    private Integer id;
    private String name;
    private String address;
    private Double rating;
    private String photosUrl;
    private Integer roomCount;
    private List<Integer> roomAvailable;
}