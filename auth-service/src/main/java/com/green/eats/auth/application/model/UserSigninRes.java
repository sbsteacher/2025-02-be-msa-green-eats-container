package com.green.eats.auth.application.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSigninRes {
    private Long id;
    private String name;
}
