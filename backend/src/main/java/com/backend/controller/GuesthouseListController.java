package com.backend.controller;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.response.GuesthouseResponseDto;
import com.backend.service.GuesthouseListService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/guesthouse")
@Validated
@Tag(name = "Guesthouse Search API", description = "게스트하우스 검색")
public class GuesthouseListController {

    private final GuesthouseListService guesthouseListService;

    public GuesthouseListController(GuesthouseListService guesthouseListService) {
        this.guesthouseListService = guesthouseListService;
    }

    @Operation(
        summary = "게스트하우스 검색",
        description = "체크인/체크아웃, 인원수, 이름(부분일치)로 게스트하우스를 검색합니다. 파라미터는 모두 선택이며 기본값이 적용됩니다."
    )
    @GetMapping("/search")
    public List<GuesthouseResponseDto> searchGuesthouses(
            @Parameter(
                name = "check_in",
                description = "체크인 날짜 (YYYY-MM-DD). 미입력 시 오늘 날짜",
                example = "2025-09-01",
                schema = @Schema(type = "string", format = "date")
            )
            @RequestParam(name = "check_in", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,

            @Parameter(
                name = "check_out",
                description = "체크아웃 날짜 (YYYY-MM-DD). 미입력 시 check_in + 1일",
                example = "2025-09-02",
                schema = @Schema(type = "string", format = "date")
            )
            @RequestParam(name = "check_out", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,

            @Parameter(
                name = "name",
                description = "게스트하우스 이름(부분검색)",
                example = "제주"
            )
            @RequestParam(name = "name", required = false) String name,

            @Parameter(
                name = "people",
                description = "인원 수 (기본값 1)",
                example = "2",
                schema = @Schema(type = "integer", format = "int32", minimum = "1")
            )
            @RequestParam(name = "people", required = false) @Min(1) Integer people,

            @Parameter(
                name = "user-id",
                in = ParameterIn.HEADER,
                required = true,
                description = "요청 사용자(호스트/게스트) ID",
                example = "1",
                schema = @Schema(type = "integer", format = "int32")
            )
            @RequestHeader("user-id") Integer userId
    ) {
        // 기본값 처리
        if (checkIn == null) checkIn = LocalDate.now();
        if (checkOut == null) checkOut = checkIn.plusDays(1);
        if (people == null) people = 1;

        return guesthouseListService.searchGuesthouses(userId, checkIn, checkOut, name, people);
    }
}
