package com.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "IdCheckResponse", description = "아이디 사용 가능 여부")
public class IdCheckResponseDto {
    @Schema(description = "사용 가능하면 true, 아니면 false", example = "true")
    public boolean available;

    public IdCheckResponseDto(boolean available) { this.available = available; }
    public IdCheckResponseDto() {}
}
