package com.example.pfe_backend.DTO;

import com.example.pfe_backend.model.User;
import lombok.Data;

@Data
public class RegistrationRequest {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private User.Role role;
}
