package com.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private Integer id;
    private String username;
    private String loginId;
    private String role;
    private String phoneNumber;
}