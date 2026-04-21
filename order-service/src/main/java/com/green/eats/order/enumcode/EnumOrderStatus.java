package com.green.eats.order.enumcode;

import com.green.eats.common.enumcode.AbstractEnumCodeConverter;
import com.green.eats.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor //DI받기 위함이 아니다.
public enum EnumOrderStatus implements EnumMapperType {
    //enum이름("코드", "값")
      PENDING("01", "결제 대기")
    , COMPLETED("02", "결제 완료")
    , CANCELLED("03", "결제 취소")
    ;

    private final String code;
    private final String value;

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<EnumOrderStatus> {
        public CodeConverter() {
            super(EnumOrderStatus.class, false); //두번째 인자값은 nullable
        }
    }
}
