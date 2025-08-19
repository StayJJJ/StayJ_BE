package com.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.dto.request.SignUpRequest;
import com.backend.entity.User;
import com.backend.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	public UserRepository userRepository;
	
	public void signUp(SignUpRequest request) {
		User user = User.builder()
		.username(request.getUsername())
		.loginId(request.getLoginId())
		.password(request.getPassword())
		.role(request.getRole())
		.phoneNumber(request.getPhoneNumber())
		.build();
		
		userRepository.save(user);
	}
}
