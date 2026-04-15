package com.green.eats.order.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id @Tsid
    private Long id;

    @Column(nullable = false)
    private Long userId; // user_cache의 ID 참조

    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @Builder
    public Order(Long userId, Long totalAmount) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.PENDING;
    }

    public void addOrderItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }

    public enum OrderStatus { PENDING, COMPLETED, CANCELLED }
}