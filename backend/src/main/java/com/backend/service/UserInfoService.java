package com.backend.service;

import org.springframework.stereotype.Service;

import com.backend.dto.response.UserInfoDto;
import com.backend.entity.User;
import com.backend.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserInfoService {
	private final UserRepository userRepository;
	private final UserFindService userFindService;

	public UserInfoDto getUserInfo(Integer userId) {
		return userFindService.findById(userId).toDto();
	}

	public UserInfoDto updateUserInfo(Integer userId, String username, String phoneNumber, String password) {
		User user = userFindService.findById(userId);
		if ((username == null || username.isBlank()) && (phoneNumber == null || phoneNumber.isBlank())
				&& (password == null || password.isBlank())) {
			throw new org.springframework.web.server.ResponseStatusException(
					org.springframework.http.HttpStatus.BAD_REQUEST, "수정할 필드가 없습니다.");
		}

		user.updateUsername(username);
		user.updatePhoneNumber(phoneNumber);
		user.updatePassword(password);

		return user.toDto();
	}
}
