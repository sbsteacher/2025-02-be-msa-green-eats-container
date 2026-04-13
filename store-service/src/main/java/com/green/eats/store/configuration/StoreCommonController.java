package com.green.eats.store.configuration;

import com.green.eats.common.CommonController;
import com.green.eats.common.enumcode.EnumMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store")
public class StoreCommonController extends CommonController {
    public StoreCommonController(EnumMapper enumMapper) {
        super(enumMapper);
    }
}
