package com.green.eats.store.application;

import com.green.eats.common.auth.UserContext;
import com.green.eats.common.model.ResultResponse;
import com.green.eats.common.model.UserDto;
import com.green.eats.store.application.model.MenuGetRes;
import com.green.eats.store.entity.Menu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping("/menu")
    public ResultResponse<?> getAllMenus() {
        UserDto userDto = UserContext.get();
        log.info("userDto: {}", userDto);

        List<MenuGetRes> menus = storeService.getAllMenus();
        return ResultResponse.builder()
                .resultMessage( String.format("%d rows", menus.size()) )
                .resultData( menus )
                .build();
    }
}
