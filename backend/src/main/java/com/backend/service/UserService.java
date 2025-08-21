package com.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.dto.request.SignUpRequest;
import com.backend.dto.response.UserResponse;
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
	
	/**
	 * 로그인 서비스 만들기
	 * 1. 아이디랑 패스워드 정보 받아오기
	 * 2. 아이디가 db에 있는지 확인
	 * 3-1. 아이디가 있으면 패스워드 맞는지 비교
	 * 3-2. 아이디가 없으면 알림창 띄우기
	 * 4. 아이디 있고 패스워드 맞으면 로그인 성공
	 * 5. 토큰 생성 
	 * */
	
	// return Id 값
	public User login(String login_id, String password) {
        // 1. 아이디로 사용자 조회
        Optional<User> optionalUser = userRepository.findByLoginId(login_id);
        
        // 아이디가 존재하지 않으면 예외 발생
        User user = optionalUser.orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        // 2. 비밀번호 확인
        if (!password.matches(user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 로그인 성공
        return user;
//		return userRepository.findAll();
	}
	
	@SuppressWarnings("deprecation")
	public UserResponse getUserById(int id) {
		
		User user = userRepository.getById(id);
		UserResponse resultUser = new UserResponse();
		resultUser.setId(user.getId());
		resultUser.setLoginId(user.getLoginId());
		resultUser.setPassword(user.getPassword());
		resultUser.setPhoneNumber(user.getPhoneNumber());
		resultUser.setRole(user.getRole());
		resultUser.setUsername(user.getUsername());
		
		
		return resultUser;
	}
}
