package com.innocito.standupai.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;
}