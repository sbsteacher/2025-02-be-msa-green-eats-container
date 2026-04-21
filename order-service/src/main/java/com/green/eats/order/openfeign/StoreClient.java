package com.green.eats.order.openfeign;

import com.green.eats.common.model.MenuGetClientRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * url: ${store-service.url:}
 * -> 'store-service.url' 값이 있으면 사용하고, 없으면 빈 문자열("")을 사용함.
 * -> 빈 문자열일 경우 Feign은 자동으로 'name'을 이용해 서비스 디스커버리를 수행함.
 */
@FeignClient(name = "store-service", url = "${constants.http.store.url:}")
public interface StoreClient {

    @GetMapping("/api/store/menu/list")
    Map<Long, MenuGetClientRes> getMenuDetail(@RequestParam("menuIds") List<Long> menuIds);
}
