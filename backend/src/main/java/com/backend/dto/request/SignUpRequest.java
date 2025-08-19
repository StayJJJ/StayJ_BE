package com.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {
    private String username;

    @JsonProperty("login_id")
    private String loginId;

    private String password;

    private String role;

    @JsonProperty("phone_number")
    private String phoneNumber;
}
