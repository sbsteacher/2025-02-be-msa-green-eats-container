package com.green.eats.order.application;

import com.green.eats.order.application.model.OrderDto;
import com.green.eats.order.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    //JPQL
    @Query("SELECT new com.green.eats.order.application.model.OrderDto(o.id, o.totalAmount, o.status, u.name) " +
            "FROM Order o " +
            "JOIN UserCache u ON o.userId = u.userId " + // 외래키 관계가 없어도 가능
            "WHERE (:lastId IS NULL OR o.id < :lastId) " +
            "ORDER BY o.id DESC")
    List<OrderDto> findAllOrderListWithUser(@Param("lastId") Long lastId, Pageable pageable);
}
