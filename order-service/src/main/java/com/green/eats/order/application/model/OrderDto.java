package com.green.eats.order.application.model;

import com.green.eats.order.enumcode.EnumOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderDto {
    private Long orderId;
    private Integer totalAmount;
    private EnumOrderStatus status;
    private String userName; // UserCache에서 가져올 이름
}
