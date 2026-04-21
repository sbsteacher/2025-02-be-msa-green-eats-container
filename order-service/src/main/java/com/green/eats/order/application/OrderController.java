package com.green.eats.order.application;

import com.green.eats.common.auth.UserContext;
import com.green.eats.common.model.ResultResponse;
import com.green.eats.common.model.UserDto;
import com.green.eats.order.application.model.OrderGetDetailRes;
import com.green.eats.order.application.model.OrderGetPageRes;
import com.green.eats.order.application.model.OrderPostReq;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResultResponse<?> postOrder(@RequestBody OrderPostReq req) {
        log.info("orderPostReq: {}", req);
        UserDto userDto = UserContext.get();
        Long orderId = orderService.postOrder(userDto.id(), req);
        return ResultResponse.builder()
                .resultMessage("success")
                .resultData(orderId)
                .build();
    }

    @GetMapping
    public ResultResponse<?> getOrderList(@RequestParam(required = false) Long lastId) {
        log.info("lastId: {}", lastId);
        OrderGetPageRes data = orderService.getOrders(lastId);
        return ResultResponse.builder()
                .resultMessage(String.format("%d rows", data.getOrders().size()))
                .resultData(data)
                .build();
    }

    @GetMapping("/{orderId}")
    public ResultResponse<?> getOrderDetail(@PathVariable Long orderId) {
        List<OrderGetDetailRes> data = orderService.getOrderDetail(orderId);
        return ResultResponse.builder()
                .resultMessage(String.format("%d rows", data.size()))
                .resultData(data)
                .build();
    }
}
