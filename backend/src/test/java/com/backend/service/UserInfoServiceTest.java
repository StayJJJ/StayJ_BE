package com.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.backend.dto.response.UserInfoDto;
import com.backend.entity.User;
import com.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFindService userFindService;

    @InjectMocks
    private UserInfoService userInfoService;

    private User testUser;
    private UserInfoDto testUserInfoDto;

    @BeforeEach
    void setUp() {
        // 테스트용 User 객체 생성
        testUser = User.builder()
                .id(1)
                .username("testUser")
                .loginId("test123")
                .password("password123")
                .role("GUEST")
                .phoneNumber("010-1234-5678")
                .build();

        // 테스트용 UserInfoDto 객체 생성 (생성자 파라미터: id, username, loginId, role, phoneNumber)
        testUserInfoDto = new UserInfoDto(1, "testUser", "test123", "GUEST", "010-1234-5678");
    }

    @Test
    @DisplayName("사용자 정보 조회 성공")
    void getUserInfo_Success() {
        // given
        Integer userId = 1;
        when(userFindService.findById(userId)).thenReturn(testUser);

        // when
        UserInfoDto result = userInfoService.getUserInfo(userId);

        // then
        assertNotNull(result);
        assertEquals(testUserInfoDto.getId(), result.getId());
        assertEquals(testUserInfoDto.getUsername(), result.getUsername());
        assertEquals(testUserInfoDto.getLoginId(), result.getLoginId());
        assertEquals(testUserInfoDto.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(testUserInfoDto.getRole(), result.getRole());
        
        verify(userFindService, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자 정보 조회 - 사용자를 찾을 수 없음")
    void getUserInfo_UserNotFound() {
        // given
        Integer userId = 999;
        when(userFindService.findById(userId)).thenThrow(new RuntimeException("User not found"));

        // when & then
        assertThrows(RuntimeException.class, () -> userInfoService.getUserInfo(userId));
        verify(userFindService, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자 정보 업데이트 - 모든 필드 업데이트")
    void updateUserInfo_AllFields_Success() {
        // given
        Integer userId = 1;
        String newUsername = "updatedUser";
        String newPhoneNumber = "010-9876-5432";
        String newPassword = "newPassword123";
        
        when(userFindService.findById(userId)).thenReturn(testUser);

        // when
        UserInfoDto result = userInfoService.updateUserInfo(userId, newUsername, newPhoneNumber, newPassword);

        // then
        assertNotNull(result);
        assertEquals(newUsername, testUser.getUsername());
        assertEquals(newPhoneNumber, testUser.getPhoneNumber());
        assertEquals(newPassword, testUser.getPassword());
        
        verify(userFindService, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자 정보 업데이트 - 사용자명만 업데이트")
    void updateUserInfo_UsernameOnly_Success() {
        // given
        Integer userId = 1;
        String newUsername = "updatedUser";
        String originalPhoneNumber = testUser.getPhoneNumber();
        String originalPassword = testUser.getPassword();
        
        when(userFindService.findById(userId)).thenReturn(testUser);

        // when
        UserInfoDto result = userInfoService.updateUserInfo(userId, newUsername, null, null);

        // then
        assertNotNull(result);
        assertEquals(newUsername, testUser.getUsername());
        assertEquals(originalPhoneNumber, testUser.getPhoneNumber()); // 변경되지 않음
        assertEquals(originalPassword, testUser.getPassword()); // 변경되지 않음
        
        verify(userFindService, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자 정보 업데이트 - 전화번호만 업데이트")
    void updateUserInfo_PhoneNumberOnly_Success() {
        // given
        Integer userId = 1;
        String newPhoneNumber = "010-9876-5432";
        String originalUsername = testUser.getUsername();
        String originalPassword = testUser.getPassword();
        
        when(userFindService.findById(userId)).thenReturn(testUser);

        // when
        UserInfoDto result = userInfoService.updateUserInfo(userId, null, newPhoneNumber, null);

        // then
        assertNotNull(result);
        assertEquals(originalUsername, testUser.getUsername()); // 변경되지 않음
        assertEquals(newPhoneNumber, testUser.getPhoneNumber());
        assertEquals(originalPassword, testUser.getPassword()); // 변경되지 않음
        
        verify(userFindService, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자 정보 업데이트 - 비밀번호만 업데이트")
    void updateUserInfo_PasswordOnly_Success() {
        // given
        Integer userId = 1;
        String newPassword = "newPassword123";
        String originalUsername = testUser.getUsername();
        String originalPhoneNumber = testUser.getPhoneNumber();
        
        when(userFindService.findById(userId)).thenReturn(testUser);

        // when
        UserInfoDto result = userInfoService.updateUserInfo(userId, null, null, newPassword);

        // then
        assertNotNull(result);
        assertEquals(originalUsername, testUser.getUsername()); // 변경되지 않음
        assertEquals(originalPhoneNumber, testUser.getPhoneNumber()); // 변경되지 않음
        assertEquals(newPassword, testUser.getPassword());
        
        verify(userFindService, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자 정보 업데이트 - 빈 문자열 필드 업데이트")
    void updateUserInfo_EmptyStrings_Success() {
        // given
        Integer userId = 1;
        String emptyUsername = "";
        String validPhoneNumber = "010-9876-5432";
        String originalUsername = testUser.getUsername();
        String originalPassword = testUser.getPassword();
        
        when(userFindService.findById(userId)).thenReturn(testUser);

        // when
        UserInfoDto result = userInfoService.updateUserInfo(userId, emptyUsername, validPhoneNumber, null);

        // then
        assertNotNull(result);
        // 빈 문자열은 update 메서드에서 무시되므로 원래 값 유지
        assertEquals(originalUsername, testUser.getUsername());
        assertEquals(validPhoneNumber, testUser.getPhoneNumber());
        assertEquals(originalPassword, testUser.getPassword());
        
        verify(userFindService, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자 정보 업데이트 실패 - 모든 필드가 null")
    void updateUserInfo_AllFieldsNull_ThrowsException() {
        // given
        Integer userId = 1;
        when(userFindService.findById(userId)).thenReturn(testUser);

        // when & then
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userInfoService.updateUserInfo(userId, null, null, null)
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("수정할 필드가 없습니다.", exception.getReason());
        
        verify(userFindService, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자 정보 업데이트 실패 - 모든 필드가 빈 문자열")
    void updateUserInfo_AllFieldsBlank_ThrowsException() {
        // given
        Integer userId = 1;
        String blankString = "   ";
        when(userFindService.findById(userId)).thenReturn(testUser);

        // when & then
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userInfoService.updateUserInfo(userId, blankString, blankString, blankString)
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("수정할 필드가 없습니다.", exception.getReason());
        
        verify(userFindService, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자 정보 업데이트 실패 - 모든 필드가 빈 문자열과 null 혼합")
    void updateUserInfo_MixedNullAndBlank_ThrowsException() {
        // given
        Integer userId = 1;
        when(userFindService.findById(userId)).thenReturn(testUser);

        // when & then
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userInfoService.updateUserInfo(userId, null, "", "   ")
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("수정할 필드가 없습니다.", exception.getReason());
        
        verify(userFindService, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자 정보 업데이트 - 사용자를 찾을 수 없음")
    void updateUserInfo_UserNotFound() {
        // given
        Integer userId = 999;
        when(userFindService.findById(userId)).thenThrow(new RuntimeException("User not found"));

        // when & then
        assertThrows(RuntimeException.class, 
            () -> userInfoService.updateUserInfo(userId, "newUsername", null, null));
        verify(userFindService, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자 정보 업데이트 - 공백 포함 유효한 문자열")
    void updateUserInfo_ValidStringWithSpaces_Success() {
        // given
        Integer userId = 1;
        String usernameWithSpaces = "new user name";
        String originalPhoneNumber = testUser.getPhoneNumber();
        String originalPassword = testUser.getPassword();
        
        when(userFindService.findById(userId)).thenReturn(testUser);

        // when
        UserInfoDto result = userInfoService.updateUserInfo(userId, usernameWithSpaces, null, null);

        // then
        assertNotNull(result);
        assertEquals(usernameWithSpaces, testUser.getUsername());
        assertEquals(originalPhoneNumber, testUser.getPhoneNumber());
        assertEquals(originalPassword, testUser.getPassword());
        
        verify(userFindService, times(1)).findById(userId);
    }
}
