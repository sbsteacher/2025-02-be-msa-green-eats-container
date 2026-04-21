package com.green.eats.order.application.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.List;

@Data
public class OrderPostReq {
    @NotEmpty(message = "주문 항목이 최소 하나는 있어야 합니다.")
    private List<OrderPostItemReq> items;

    @Positive(message = "0 이상이어야 합니다.")
    private Long totalAmount;
}
