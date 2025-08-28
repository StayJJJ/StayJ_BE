package com.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.LoginRequest;
import com.backend.dto.request.SignUpRequest;
import com.backend.dto.response.IdCheckResponseDto;
import com.backend.dto.response.SuccessResponse;
import com.backend.dto.response.UserInfoDto;
import com.backend.dto.response.UserResponse;
import com.backend.entity.User;
import com.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

	// 0) 아이디 중복 확인
	@Operation(
        summary = "아이디 중복 확인",
        description = "쿼리 파라미터로 받은 login_id가 사용 가능한지 여부를 반환합니다. " +
                      "이미 존재하면 false, 사용 가능하면 true 입니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "확인 성공",
                content = @Content(schema = @Schema(implementation = IdCheckResponseDto.class),
                    examples = {
                        @ExampleObject(name = "사용 가능", value = "{ \"available\": true }"),
                        @ExampleObject(name = "이미 존재", value = "{ \"available\": false }")
                    }
                )
            ),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청", content = @Content)
        }
    )
    @GetMapping("/check-id")
    public ResponseEntity<IdCheckResponseDto> checkId(
        @Parameter(
            name = "login_id",
            description = "중복 확인할 로그인 아이디",
            required = true,
            in = ParameterIn.QUERY,
            example = "hong123"
        )
        @RequestParam("login_id") String loginId
    ) {
        boolean available = userService.isLoginIdAvailable(loginId);
        return ResponseEntity.ok(new IdCheckResponseDto(available));
    }
	
    // ---------------------------------------------------------
    // 1) 회원가입
    // ---------------------------------------------------------
    @Operation(
        summary = "회원가입",
        description = "새로운 사용자를 회원가입시킵니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "회원가입 요청",
            required = true,
            content = @Content(
                schema = @Schema(implementation = SignUpRequest.class),
                examples = @ExampleObject(
                    name = "회원가입 예시",
                    value = """
                    {
                      "username": "홍길동",
                      "login_id": "hong123",
                      "password": "1234",
                      "role": "GUEST",
                      "phone_number": "010-1234-5678"
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청", content = @Content)
        }
    )
    @PostMapping("/sign-up")
    public ResponseEntity<SuccessResponse> signUp(@RequestBody SignUpRequest request) {
        userService.signUp(request);
        return ResponseEntity.ok(new SuccessResponse(true));
    }

    // ---------------------------------------------------------
    // 2) 로그인
    // ---------------------------------------------------------
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

    // ---------------------------------------------------------
    // 3) 사용자 단건 조회
    // ---------------------------------------------------------
    @Operation(
        summary = "사용자 단건 조회",
        description = "사용자 ID로 사용자 정보를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
        }
    )
    @GetMapping("/{id}")
    public UserResponse getUserById(
        @Parameter(
            name = "id",
            in = ParameterIn.PATH,
            required = true,
            description = "조회할 사용자 ID",
            example = "1",
            schema = @Schema(type = "integer", format = "int32")
        )
        @PathVariable("id") int id
    ) {
        return userService.getUserById(id);
    }
}
