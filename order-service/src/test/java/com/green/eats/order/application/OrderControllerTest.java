package com.green.eats.order.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.eats.common.auth.UserContext;
import com.green.eats.common.model.UserDto;
import com.green.eats.order.application.model.OrderGetDetailRes;
import com.green.eats.order.application.model.OrderGetPageRes;
import com.green.eats.order.application.model.OrderPostReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import org.springframework.test.web.servlet.ResultActions;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import static org.mockito.BDDMockito.given;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get; // 중요: pathParameters 사용 시 필수

import static org.springframework.restdocs.request.RequestDocumentation.*;

@WebMvcTest(
        value = OrderController.class,
        excludeAutoConfiguration = com.green.eats.common.enumcode.EnumAutoConfiguration.class
)
@AutoConfigureRestDocs
// 💡 1. Security 인증 필터를 패스하여 순수 컨트롤러 명세 추출에만 집중합니다.
@AutoConfigureMockMvc(addFilters = false)
// 💡 2. Spring Boot 4.0의 슬라이스 테스트 내 HTTP 메시지 컨버터 유실 버그를 방어합니다.
@ImportAutoConfiguration(HttpMessageConvertersAutoConfiguration.class)
class OrderControllerTest {
    @TestConfiguration
    static class TestConfig {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 💡 Spring Boot 4 규격: @MockBean 대신 @MockitoBean을 사용합니다.
    @MockitoBean
    private OrderService orderService;

    @Test
    @DisplayName("주문 생성 API - 성공 (REST Docs + Swagger)")
    void postOrderSuccess() throws Exception {
        // given
        OrderPostReq request = new OrderPostReq();
        request.setItems(new ArrayList<>()); // 테스트용 빈 리스트 (실제 환경에 맞게 MockItem 추가 가능)
        request.setTotalAmount(25000);

        Long mockOrderId = 100L;
        UserDto mockUserDto = new UserDto(1L, "홍길동"); // Record 구조 가정

        // Service Mocking
        when(orderService.postOrder(eq(1L), any(OrderPostReq.class))).thenReturn(mockOrderId);

        // UserContext Static Method Mocking
        try (MockedStatic<UserContext> userContextMockedStatic = mockStatic(UserContext.class)) {
            userContextMockedStatic.when(UserContext::get).thenReturn(mockUserDto);

            // when
            ResultActions result = mockMvc.perform(post("/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultMessage").value("success"))
                    .andExpect(jsonPath("$.resultData").value(mockOrderId))

                    // REST Docs & Swagger 문서화 설정
                    .andDo(document("post-order", // 문서 식별자
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Order") // Swagger UI에서 그룹핑될 태그 이름
                                    .summary("주문 생성 API") // 스웨거 한줄 요약
                                    .description("사용자의 장바구니 상품들을 이용해 주문을 등록합니다.") // 상세 설명
                                    .requestFields(
                                            fieldWithPath("items").type(JsonFieldType.ARRAY).description("주문 상품 목록 (최소 1개 이상)"),
                                            fieldWithPath("totalAmount").type(JsonFieldType.NUMBER).description("총 주문 금액 (0보다 커야 함)")
                                    )
                                    .responseFields(
                                            fieldWithPath("resultMessage").type(JsonFieldType.STRING).description("결과 메시지 (success)"),
                                            fieldWithPath("resultData").type(JsonFieldType.NUMBER).description("생성 완료된 주문 ID")
                                    )
                                    .build()
                            )
                    ));
        }
    }

    @Test
    @DisplayName("주문 목록 조회 API 성공 테스트 및 문서화")
    void getOrderListSuccess() throws Exception {
        // given
        Long lastId = 10L;

        // OrderDto 구조를 유추하여 Mock 데이터 생성 (필요시 실제 OrderDto 필드에 맞게 빌더 등으로 수정)
        OrderGetPageRes mockResponse = new OrderGetPageRes(List.of(), false, 1L);

        given(orderService.getOrders(any())).willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/")
                        .param("lastId", String.valueOf(lastId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("order-get-list", // 조각(snippet) 파일이 저장될 디렉토리명
                        queryParameters(
                                parameterWithName("lastId").description("마지막으로 조회된 주문 ID (커서 기반 페이징용, 선택 항목)").optional()
                        ),
                        responseFields(
                                fieldWithPath("resultMessage").description("결과 메시지 (예: '0 rows')"),
                                fieldWithPath("resultData").description("결과 데이터 전체"),
                                fieldWithPath("resultData.orders").description("주문 정보 배열 (OrderDto 목록)"),
                                fieldWithPath("resultData.hasNext").description("다음 페이지 존재 여부"),
                                fieldWithPath("resultData.nextLastId").description("다음 페이징 요청 시 사용할 lastId (null 가능)").optional()
                        )
                ));
    }

    @Test
    @DisplayName("주문 상세 조회 API 성공 테스트 및 문서화")
    void getOrderDetailSuccess() throws Exception {
        // given
        Long orderId = 1L;
        List<OrderGetDetailRes> mockResponse = List.of(
                OrderGetDetailRes.builder().id(1L).name("맛있는 치킨").price(20000).quantity(1).build(),
                OrderGetDetailRes.builder().id(2L).name("시원한 콜라").price(2500).quantity(2).build()
        );

        given(orderService.getOrderDetail(orderId)).willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/{orderId}", orderId) // RestDocumentationRequestBuilders.get 사용 필수
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("order-get-detail",
                        pathParameters(
                                parameterWithName("orderId").description("조회할 주문 ID")
                        ),
                        responseFields(
                                fieldWithPath("resultMessage").description("결과 메시지 (예: '2 rows')"),
                                fieldWithPath("resultData").description("결과 데이터 목록 (주문 상세 정보 배열)"),
                                fieldWithPath("resultData[].id").description("상품 상세 ID"),
                                fieldWithPath("resultData[].name").description("상품명"),
                                fieldWithPath("resultData[].price").description("가격"),
                                fieldWithPath("resultData[].quantity").description("수량")
                        )
                ));
    }

}