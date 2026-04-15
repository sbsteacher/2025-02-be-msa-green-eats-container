package com.green.eats.auth.application.model;

import com.green.eats.common.model.EnumUserRole;
import lombok.Data;

@Data
public class UserSignupReq {
    private String email;
    private String password;
    private String name;
    private String address;
    private EnumUserRole userRole;
}
