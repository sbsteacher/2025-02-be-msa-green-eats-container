package com.green.eats.store.application.model;

import com.green.eats.store.enumcode.EnumMenuCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MenuPostReq {
    @NotBlank
    private String name;
    @Positive
    private Integer price;
    private Integer stockQuantity;
    @NotNull
    private EnumMenuCategory menuCategory;
}
