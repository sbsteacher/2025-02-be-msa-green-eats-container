package com.green.eats.order.application;

import com.green.eats.common.auth.UserContext;
import com.green.eats.common.model.ResultResponse;
import com.green.eats.common.model.UserDto;
import com.green.eats.order.application.model.OrderPostReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResultResponse<?> placeOrder(@RequestBody OrderPostReq req) {
        log.info("orderPostReq: {}", req);
        UserDto userDto = UserContext.get();
        Long orderId = orderService.postOrder(userDto.id(), req);
        return ResultResponse.builder()
                .resultMessage("success")
                .resultData(orderId)
                .build();
    }
}
