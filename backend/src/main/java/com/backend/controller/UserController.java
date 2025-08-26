package com.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.LoginRequest;
import com.backend.dto.request.SignUpRequest;
import com.backend.dto.response.UserInfoDto;
import com.backend.dto.response.UserResponse;
import com.backend.entity.User;
import com.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
@Tag(name = "User API", description = "사용자 회원가입/로그인/조회 API")
public class UserController {

    @Autowired
    public UserService userService;

    @Operation(
        summary = "로그인",
        description = "아이디와 비밀번호로 로그인 후 사용자 정보를 반환합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                schema = @Schema(implementation = LoginRequest.class),
                examples = @ExampleObject(
                    name = "로그인 예시",
                    value = "{\n  \"login_id\": \"guest01\",\n  \"password\": \"pw1234\"\n}"
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                content = @Content(schema = @Schema(implementation = UserInfoDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인 실패", content = @Content)
        }
    )
    @PostMapping("/login")
    public ResponseEntity<UserInfoDto> login(@Valid @RequestBody LoginRequest request) {
        System.out.println("LOGIN >>> loginId=" + request.getLoginId() + ", pw=" + request.getPassword());

        User user = userService.login(request.getLoginId(), request.getPassword());
        return ResponseEntity.ok(new UserInfoDto(
            user.getId(),
            user.getUsername(),
            user.getLoginId(),
            user.getRole(),
            user.getPhoneNumber()
        ));
    }

    // (회원가입/조회는 기존 그대로 두셔도 됩니다)
}
