package com.backend.service;

import com.backend.dto.response.UserInfoDto;
import com.backend.entity.User;
import com.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserFindServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserFindService userFindService;

    @Test
    @DisplayName("존재하는 유저 ID로 조회하면 User 엔티티를 반환한다")
    void findById_success() {
        // given
        User mockUser = User.builder()
                .id(1)
                .username("홍길동")
                .loginId("hong123")
                .password("encryptedPassword")
                .role("HOST")
                .phoneNumber("010-1234-5678")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));

        // when
        User result = userFindService.findById(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUsername()).isEqualTo("홍길동");
        assertThat(result.getLoginId()).isEqualTo("hong123");
        assertThat(result.getRole()).isEqualTo("HOST");
        assertThat(result.getPhoneNumber()).isEqualTo("010-1234-5678");

        verify(userRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("User 엔티티를 UserInfoDto로 변환하면 DTO 필드가 올바르게 매핑된다")
    void toDto_success() {
        // given
        User mockUser = User.builder()
                .id(2)
                .username("김철수")
                .loginId("kimcs")
                .password("pass")
                .role("GUEST")
                .phoneNumber("010-9999-8888")
                .build();

        // when
        UserInfoDto dto = mockUser.toDto();

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getUsername()).isEqualTo("김철수");
        assertThat(dto.getLoginId()).isEqualTo("kimcs");
        assertThat(dto.getRole()).isEqualTo("GUEST");
        assertThat(dto.getPhoneNumber()).isEqualTo("010-9999-8888");
    }

    @Test
    @DisplayName("존재하지 않는 유저 ID로 조회하면 IllegalArgumentException 발생")
    void findById_notFound() {
        // given
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userFindService.findById(99))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found: 99");

        verify(userRepository, times(1)).findById(99);
    }
}
