package com.green.eats.order.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class OrderItem {
    @Id
    @Tsid
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Long menuId;
    private Integer quantity;
    private Long price;

    @Builder
    public OrderItem(Long menuId, Integer quantity, Long price) {
        this.menuId = menuId;
        this.quantity = quantity;
        this.price = price;
    }

    public void setOrder(Order order) { this.order = order; }
}