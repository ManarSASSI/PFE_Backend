package com.example.pfe_backend.DTO;

import lombok.Data;
@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
