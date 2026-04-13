package com.green.eats.auth.application.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserSigninReq {
    @Email @NotBlank
    private String email;

    @NotBlank
    private String password;
}
