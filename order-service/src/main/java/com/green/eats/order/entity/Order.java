package com.green.eats.order.entity;

import com.green.eats.order.enumcode.EnumOrderStatus;
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

    private Integer totalAmount;

    @Column(length = 2, nullable = false)
    private EnumOrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @Builder
    public Order(Long userId, Integer totalAmount) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = EnumOrderStatus.COMPLETED;
    }

    public void addOrderItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }


}