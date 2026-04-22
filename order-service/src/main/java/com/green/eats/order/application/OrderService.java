package com.green.eats.order.application;

import com.green.eats.common.exception.BusinessException;
import com.green.eats.common.exception.CommonErrorCode;
import com.green.eats.common.model.MenuGetClientRes;
import com.green.eats.order.application.model.OrderDto;
import com.green.eats.order.application.model.OrderGetDetailRes;
import com.green.eats.order.application.model.OrderGetPageRes;
import com.green.eats.order.application.model.OrderPostReq;
import com.green.eats.order.entity.Order;
import com.green.eats.order.entity.OrderItem;
import com.green.eats.order.exception.OrderErrorCode;
import com.green.eats.order.openfeign.StoreClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserCacheRepository userCacheRepository;
    private final StoreClient storeClient;

    @Transactional
    public Long postOrder(Long userId, OrderPostReq req) {
        userCacheRepository.findById(userId)
                .orElseThrow( () -> new BusinessException( CommonErrorCode.NO_EXISTED_USER ) );

        Integer totalAmount = req.getItems().stream()
                                         .mapToInt(item -> item.getQuantity() * item.getPrice())
                                         .sum();

        if(!totalAmount.equals(req.getTotalAmount())) { //Long != Long, ==, != 비교 안 된다.
            throw BusinessException.of( OrderErrorCode.NOT_MATCHED_ALL_AMOUNT );
        }

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

        return orderRepository.save(order).getId(); //insert 후 pk값 리턴
    }

    public OrderGetPageRes getOrders(Long lastId) {
        int pageSize = 20;

        PageRequest pageRequest = PageRequest.of(0, pageSize); //JPA 페이징 처리, 이 코드에서는 LIMIT 역할만 한다.

        List<OrderDto> orders = orderRepository.findAllOrderListWithUser(lastId, pageRequest);

        // 가져온 갯수와 pageSize가 같다면 다음 페이지가 있을 확률이 높음!
        boolean hasNext = orders.size() == pageSize;

        Long nextLastId = orders.isEmpty() ? null : orders.get(orders.size() - 1).getOrderId();

        return new OrderGetPageRes(orders, hasNext, nextLastId);
    }

    public List<OrderGetDetailRes> getOrderDetail(Long orderId) {
        List<OrderItem> orderList = orderItemRepository.findAllByOrderId(orderId);
        List<Long> menuIds = orderList.stream()
                                      //.map(OrderItem::getMenuId)
                                      .map( item -> item.getMenuId() )
                                      .toList();
        Map<Long, MenuGetClientRes> menuMap = storeClient.getMenuDetail(menuIds);

        List<OrderGetDetailRes> list = new ArrayList<>(orderList.size());
        for(OrderItem item : orderList) {
            OrderGetDetailRes res = OrderGetDetailRes.builder()
                                                    .id(item.getId())
                                                    .name(menuMap.get(item.getMenuId()).getName())
                                                    .price(item.getPrice())
                                                    .quantity(item.getQuantity())
                                                    .build();

            list.add(res);
        }
        return list;

//        return orderList.stream().map(item -> OrderGetDetailRes.builder()
//                .id(item.getId())
//                .name(menuMap.get(item.getMenuId()).getName())
//                .price(item.getPrice())
//                .quantity(item.getQuantity())
//                .build()
//        ).toList();
    }
}
