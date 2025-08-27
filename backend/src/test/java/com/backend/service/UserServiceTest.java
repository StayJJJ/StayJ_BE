package com.backend.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.dto.request.SignUpRequest;
import com.backend.dto.response.UserResponse;
import com.backend.entity.User;
import com.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private SignUpRequest signUpRequest;

    @BeforeEach
    void setUp() {
        // 테스트용 User 객체 생성
        testUser = User.builder()
                .id(1)
                .username("testUser")
                .loginId("test123")
                .password("password123")
                .role("USER")
                .phoneNumber("010-1234-5678")
                .build();

        // 테스트용 SignUpRequest 객체 생성
        signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("newUser");
        signUpRequest.setLoginId("new123");
        signUpRequest.setPassword("newPassword");
        signUpRequest.setRole("USER");
        signUpRequest.setPhoneNumber("010-9876-5432");
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signUp_Success() {
        // given
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        assertDoesNotThrow(() -> userService.signUp(signUpRequest));

        // then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_Success() {
        // given
        String loginId = "test123";
        String password = "password123";
        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.of(testUser));

        // when
        User result = userService.login(loginId, password);

        // then
        assertNotNull(result);
        assertEquals(testUser.getLoginId(), result.getLoginId());
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findByLoginId(loginId);
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 아이디")
    void login_UserNotFound() {
        // given
        String loginId = "nonexistent";
        String password = "password123";
        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.login(loginId, password)
        );
        assertEquals("아이디가 존재하지 않습니다.", exception.getMessage());
        verify(userRepository, times(1)).findByLoginId(loginId);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_WrongPassword() {
        // given
        String loginId = "test123";
        String wrongPassword = "wrongPassword";
        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.of(testUser));

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.login(loginId, wrongPassword)
        );
        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
        verify(userRepository, times(1)).findByLoginId(loginId);
    }

    @Test
    @DisplayName("사용자 ID로 조회 성공 테스트")
    void getUserById_Success() {
        // given
        int userId = 1;
        when(userRepository.getById(userId)).thenReturn(testUser);

        // when
        UserResponse result = userService.getUserById(userId);

        // then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getLoginId(), result.getLoginId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getPassword(), result.getPassword());
        assertEquals(testUser.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(testUser.getRole(), result.getRole());
        verify(userRepository, times(1)).getById(userId);
    }

    @Test
    @DisplayName("사용자 ID로 조회 - 존재하지 않는 사용자")
    void getUserById_UserNotFound() {
        // given
        int userId = 999;
        when(userRepository.getById(userId)).thenThrow(new RuntimeException("User not found"));

        // when & then
        assertThrows(RuntimeException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).getById(userId);
    }

    @Test
    @DisplayName("회원가입 - null 값 처리 테스트")
    void signUp_WithNullValues() {
        // given
        SignUpRequest nullRequest = new SignUpRequest();
        // 모든 필드가 null인 상태

        // when & then
        assertDoesNotThrow(() -> userService.signUp(nullRequest));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 - null 비밀번호 테스트")
    void login_NullPassword() {
        // given
        String loginId = "test123";
        String nullPassword = null;
        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.of(testUser));

        // when & then
        assertThrows(NullPointerException.class, 
            () -> userService.login(loginId, nullPassword));
    }

    @Test
    @DisplayName("로그인 - 빈 문자열 비밀번호 테스트")
    void login_EmptyPassword() {
        // given
        String loginId = "test123";
        String emptyPassword = "";
        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.of(testUser));

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.login(loginId, emptyPassword)
        );
        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }
}