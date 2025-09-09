package com.innocito.standupai.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ResetPassword {
    private String username;
    private String newPassword;
}
