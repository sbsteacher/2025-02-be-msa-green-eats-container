package com.green.eats.common;

import com.green.eats.common.enumcode.EnumMapper;
import com.green.eats.common.enumcode.EnumMapperValue;
import com.green.eats.common.model.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CommonController {
    private final EnumMapper enumMapper;

    @GetMapping("code")
    public ResultResponse getCodeList(@RequestParam("code_type") String codeType) {
        List<EnumMapperValue> enumCodeList = enumMapper.get(codeType);
        return ResultResponse.builder()
                .resultMessage( String.format("%d rows", enumCodeList.size()) )
                .resultData(enumCodeList)
                .build();
    }
}
