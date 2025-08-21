package com.backend.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private Integer id;
    private String loginId;
    private String username;
    private String password;
    private String role = "GUEST"; // 'HOST' or 'GUEST'
    private String phoneNumber;

}
