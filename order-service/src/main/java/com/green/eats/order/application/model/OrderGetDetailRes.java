package com.green.eats.order.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderGetDetailRes {
    private Long id;
    private String name;
    private Integer price;
    private Integer quantity;
}
