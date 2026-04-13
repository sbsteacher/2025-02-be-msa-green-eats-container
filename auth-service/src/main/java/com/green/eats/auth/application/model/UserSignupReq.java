package com.green.eats.auth.application.model;

import lombok.Data;

@Data
public class UserSignupReq {
    private String email;
    private String password;
    private String name;
    private String address;
}
