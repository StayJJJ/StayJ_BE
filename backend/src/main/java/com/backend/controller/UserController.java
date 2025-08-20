package com.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.backend.dto.request.SignUpRequest;
import com.backend.service.UserService;

@Controller
@ResponseBody
@RequestMapping("/user")
public class UserController {
	@Autowired
	public UserService userService;
	
	@PostMapping("/sign-up")
	public void signUp(@RequestBody SignUpRequest request) {
		userService.signUp(request);
	}
	
	@GetMapping("/login")
	public void login() {
		
	}
}
