package com.edubridge.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoginRequest {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Size(max = 100, message = "Email too long")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 1, message = "Password cannot be empty")
    private String password;
}