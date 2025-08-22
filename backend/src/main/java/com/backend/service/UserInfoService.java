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
        user.updateUserInfo(username, phoneNumber, password);
        
        return user.toDto();
    }
}
