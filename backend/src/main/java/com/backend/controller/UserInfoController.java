package com.backend.controller;

import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.UpdateUserRequest;
import com.backend.dto.response.UserInfoDto;
import com.backend.service.UserInfoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user-info")
@RequiredArgsConstructor
@Tag(name = "User Info API", description = "내 정보 조회/수정 API")
public class UserInfoController {

    private final UserInfoService userInfoService;

    @Operation(
        summary = "내 정보 조회",
        description = "헤더의 user-id로 현재 사용자 정보를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = UserInfoDto.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
        }
    )
    @GetMapping
    public UserInfoDto getUserInfo(
        @Parameter(
            name = "user-id",
            in = ParameterIn.HEADER,
            required = true,
            description = "요청 사용자 ID",
            example = "1",
            schema = @Schema(type = "integer", format = "int32")
        )
        @RequestHeader("user-id") Integer userId
    ) {
        return userInfoService.getUserInfo(userId);
    }

    @Operation(
        summary = "내 정보 수정",
        description = "사용자 이름/전화번호/비밀번호 중 전달된 항목만 부분 수정(PATCH)합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "수정할 필드만 포함하세요(부분 업데이트).",
            required = true,
            content = @Content(
                schema = @Schema(implementation = UpdateUserRequest.class),
                examples = {
                    @ExampleObject(
                        name = "이름/전화번호 변경",
                        value = """
                        {
                          "username": "홍길동",
                          "phoneNumber": "010-1234-5678",
                          "password": "1234"
                        }
                        """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                content = @Content(schema = @Schema(implementation = UserInfoDto.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
        }
    )
    @PatchMapping
    public UserInfoDto updateUserInfo(
        @Parameter(
            name = "user-id",
            in = ParameterIn.HEADER,
            required = true,
            description = "요청 사용자 ID",
            example = "1",
            schema = @Schema(type = "integer", format = "int32")
        )
        @RequestHeader("user-id") Integer userId,

        @org.springframework.web.bind.annotation.RequestBody UpdateUserRequest request
    ) {
        return userInfoService.updateUserInfo(
            userId,
            request.getUsername(),
            request.getPhoneNumber(),
            request.getPassword()
        );
    }
}
