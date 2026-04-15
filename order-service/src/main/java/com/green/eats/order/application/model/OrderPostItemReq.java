package com.green.eats.order.application.model;

import lombok.Data;

@Data
public class OrderPostItemReq {
    private Long menuId;
    private Integer quantity;
    private Long price; // 단가
}
