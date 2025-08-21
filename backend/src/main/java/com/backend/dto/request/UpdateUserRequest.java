package com.backend.dto.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String username;
    private String phoneNumber;
    private String password;
}