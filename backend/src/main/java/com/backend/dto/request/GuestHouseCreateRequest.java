package com.backend.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestHouseCreateRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String address;

    @NotNull
    @DecimalMin("0.0") @DecimalMax("5.0")
    private Double rating;

    @JsonProperty("photo_id")
    @NotNull
    private Integer photoId;

    @JsonProperty("phone_number")
    @NotBlank
    private String phoneNumber;

    @JsonProperty("room_count")
    @NotNull @Min(1)
    private Integer roomCount;

    @NotNull @Size(min = 1)
    @Valid
    private List<RoomRequest> rooms;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomRequest {
        @NotBlank
        private String name;

        @NotNull @Min(1)
        private Integer capacity;

        @NotNull @Min(0)
        private Integer price;

        @JsonProperty("photo_id")
        @NotNull
        private Integer photoId;
    }
}
