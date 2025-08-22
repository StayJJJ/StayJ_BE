package com.backend.service;

import org.springframework.stereotype.Service;

import com.backend.entity.User;
import com.backend.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFindService {
    private final UserRepository userRepository;
    
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }
}
