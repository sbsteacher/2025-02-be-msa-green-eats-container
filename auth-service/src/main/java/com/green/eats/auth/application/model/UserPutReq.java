package com.green.eats.auth.application.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPutReq {
    @NotBlank
    private String address;
    @NotBlank
    private String name;
}
