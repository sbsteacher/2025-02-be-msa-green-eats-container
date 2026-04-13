package com.green.eats.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ResultResponse <T> {
    private String resultMessage;
    private T resultData;
}
