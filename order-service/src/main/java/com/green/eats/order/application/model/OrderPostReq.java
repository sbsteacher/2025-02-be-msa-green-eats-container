package com.green.eats.order.application.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class OrderPostReq {
    @NotEmpty(message = "주문 항목이 최소 하나는 있어야 합니다.")
    private List<OrderPostItemReq> items;
}
