package com.example.pfe_backend.DTO;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
