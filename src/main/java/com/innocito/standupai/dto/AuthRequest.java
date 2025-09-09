package com.innocito.standupai.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;

    @Column(unique = true)
    private String email;

    @NotBlank
    private String fullName;

    @NotBlank
    private String role;

}