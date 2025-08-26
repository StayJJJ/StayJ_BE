package com.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class LoginRequest {
    @JsonProperty("login_id")
    private String loginId;

    private String password;
}
