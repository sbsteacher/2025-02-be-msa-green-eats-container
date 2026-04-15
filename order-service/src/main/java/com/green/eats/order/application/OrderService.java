package com.green.eats.order.application;

import com.green.eats.order.application.model.OrderPostReq;
import com.green.eats.order.entity.Order;
import com.green.eats.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserCacheRepository userCacheRepository;

    @Transactional
    public Long postOrder(Long userId, OrderPostReq req) {
        userCacheRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));

        Long totalAmount = req.getItems().stream()
                                         .mapToLong(item -> item.getQuantity() * item.getPrice())
                                         .sum();

        // 1. 주문 마스터 생성
        Order order = Order.builder()
                .userId( userId )
                .totalAmount( totalAmount )
                .build();

        // 2. 상세 항목(List) 순회하며 추가
        req.getItems().forEach(itemReq -> {
            OrderItem item = OrderItem.builder()
                    .menuId(itemReq.getMenuId())
                    .quantity(itemReq.getQuantity())
                    .price(itemReq.getPrice())
                    .build();

            order.addOrderItem(item); // 연관 관계 편의 메소드 활용
        });

        return orderRepository.save(order).getId();
    }
}
