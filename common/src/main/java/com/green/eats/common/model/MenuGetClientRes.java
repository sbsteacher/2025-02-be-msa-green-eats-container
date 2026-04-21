package com.green.eats.common.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuGetClientRes {
    private Long menuId;
    private String name;
    private Integer price;
}
