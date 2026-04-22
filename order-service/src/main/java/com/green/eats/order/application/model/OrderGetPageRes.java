package com.green.eats.order.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderGetPageRes {
    private List<OrderDto> orders;
    private boolean hasNext;
    private Long nextLastId;

}
