package com.backend.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder

public class ReservationListItemDto {
    private Integer id;
    private Integer roomId;
    private GuestSimpleDto guest;   // { id, username }
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer peopleCount;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GuestSimpleDto {
        private Integer id;
        private String username;
    }
}
