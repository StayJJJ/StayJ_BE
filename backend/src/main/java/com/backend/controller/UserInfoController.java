package com.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.UpdateUserRequest;
import com.backend.dto.response.UserInfoDto;
import com.backend.service.UserInfoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user-info")
@RequiredArgsConstructor
public class UserInfoController {
    private final UserInfoService userInfoService;
	
	@GetMapping
	public UserInfoDto getUserInfo(@RequestHeader("user-id") Integer userId) {
	    return userInfoService.getUserInfo(userId);
	}
	
	@PutMapping
    public UserInfoDto updateUserInfo(
    		@RequestHeader("user-id") Integer userId, 
    		@RequestBody UpdateUserRequest request
    		) {
		return userInfoService.updateUserInfo(userId, request.getUsername(),
                request.getPhoneNumber(), request.getPassword());
    }
}	
