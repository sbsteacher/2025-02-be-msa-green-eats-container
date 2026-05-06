package com.green.eats.common.model;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ResultResponse <T> {
    private String resultMessage;
    private T resultData;
}
