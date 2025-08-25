package com.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdateUserRequest {
    public String username;
    @JsonProperty("phone_number")
    public String phoneNumber;
    public String password;
}