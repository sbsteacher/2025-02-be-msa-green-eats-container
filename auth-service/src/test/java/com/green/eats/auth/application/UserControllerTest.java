package com.green.eats.auth.application;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.green.eats.auth.application.model.UserPutReq;
import com.green.eats.auth.application.model.UserSigninReq;
import com.green.eats.auth.application.model.UserSignupReq;
import com.green.eats.auth.entity.User;
import com.green.eats.common.enumcode.EnumAutoConfiguration;
import com.green.eats.common.model.EnumUserRole;
import com.green.eats.common.auth.UserContext;
import com.green.eats.common.model.UserDto;
import com.green.eats.common.security.JwtTokenManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;

@WebMvcTest(UserController.class)
@AutoConfigureRestDocs
// 💡 1. Security 인증 필터를 패스하여 순수 컨트롤러 명세 추출에만 집중합니다.
@AutoConfigureMockMvc(addFilters = false)
// 💡 2. Spring Boot 4.0의 슬라이스 테스트 내 HTTP 메시지 컨버터 유실 버그를 방어합니다.
@ImportAutoConfiguration(org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration.class)
// 🎯 [최종 교정] 진범인 커스텀 EnumAutoConfiguration을 자동 설정 목록에서 완전히 제외시킵니다!
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=" +
                "com.green.eats.common.enumcode.EnumAutoConfiguration," + // 👈 이 녀석이 핵심입니다!
                "org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration," +

                "org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration," +

                "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration," +

                "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration," +
                //"org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
                "org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration"

})
class UserControllerTest {

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
    private UserService userService;

    @MockitoBean
    private JwtTokenManager jwtTokenManager;

    @Test
    @DisplayName("회원가입 API 명세화")
    void signup() throws Exception {
        // given
        UserSignupReq req = new UserSignupReq();
        req.setEmail("testuser@gmail.com");
        req.setPassword("password123!");
        req.setName("홍길동");

        doNothing().when(userService).signup(any(UserSignupReq.class));

        // when & then
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-signup",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Sign")
                                .summary("회원가입 API")
                                .description("새로운 사용자 계정을 생성합니다.")
                                .requestFields(
                                        fieldWithPath("email").description("사용자 이메일"),
                                        fieldWithPath("password").description("비밀번호"),
                                        fieldWithPath("name").description("사용자 이름"),
                                        fieldWithPath("address").description("주소").type(JsonFieldType.NULL).optional(),
                                        fieldWithPath("userRole").description("사용자 권한").type(JsonFieldType.NULL).optional()
                                )
                                .responseFields(
                                        fieldWithPath("resultMessage").description("결과 메시지"),
                                        fieldWithPath("resultData").description("결과 데이터 (1: 성공)")
                                )
                                .build()
                        )));
    }

    @Test
    @DisplayName("로그인 API 명세화")
    void signin() throws Exception {
        // given
        UserSigninReq req = new UserSigninReq();
        req.setEmail("testuser@gmail.com");
        req.setPassword("password123!");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("홍길동");
        mockUser.setEnumUserRole(EnumUserRole.USER);

        given(userService.signin(any(UserSigninReq.class))).willReturn(mockUser);
        doNothing().when(jwtTokenManager).issue(any(), any());

        // when & then
        mockMvc.perform(post("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(document("user-signin",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Sign")
                                .summary("로그인 API")
                                .description("아이디와 비밀번호로 인증 후 쿠키 토큰을 발급합니다.")
                                .requestFields(
                                        fieldWithPath("email").description("사용자 아이디"),
                                        fieldWithPath("password").description("비밀번호")
                                )
                                .responseFields(
                                        fieldWithPath("resultMessage").description("결과 메시지"),
                                        fieldWithPath("resultData.id").description("로그인한 사용자 PK"),
                                        fieldWithPath("resultData.name").description("로그인한 사용자 이름")
                                )
                                .build()
                        )));
    }

    @Test
    @DisplayName("회원정보 수정 API 명세화")
    void updUser() throws Exception {
        // given
        UserPutReq req = new UserPutReq();
        req.setName("이순신");
        req.setAddress("서울시 강남구");  // 필수라면 추가
        doNothing().when(userService).updUser(any(Long.class), any(UserPutReq.class));

        // 💡 ThreadLocal 기반의 UserContext 스태틱 메서드를 Mocking 합니다.
        try (MockedStatic<UserContext> userContextMockedStatic = mockStatic(UserContext.class)) {
            UserDto mockUserDto = new UserDto(1L, "홍길동");
            userContextMockedStatic.when(UserContext::get).thenReturn(mockUserDto);

            // when & then
            mockMvc.perform(put("/")
                            .header("X-User-Id", "1")
                            .header("X-User-Name", "홍길동")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document("user-update",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("User")
                                    .summary("회원정보 수정 API")
                                    .description("현재 로그인한 사용자의 정보를 수정합니다. (인터셉터 인증 필요)")
                                    .requestHeaders(
                                            headerWithName("X-User-Id").description("Gateway에서 주입하는 인증된 사용자 ID"),
                                            headerWithName("X-User-Name").description("Gateway에서 주입하는 인증된 사용자 이름 (URL 인코딩됨)")
                                    )
                                    .requestFields(
                                            fieldWithPath("name").description("변경할 이름"),
                                            fieldWithPath("address").description("변경할 주소")
                                    )
                                    .responseFields(
                                            fieldWithPath("resultMessage").description("결과 메시지"),
                                            fieldWithPath("resultData").description("결과 데이터 (null)").type(JsonFieldType.NULL).optional()
                                    )
                                    .build()
                            )));
        }
    }

    @Test
    @DisplayName("회원 탈퇴 API 명세화")
    void delUser() throws Exception {
        // given
        doNothing().when(userService).delUser(any(Long.class));

        try (MockedStatic<UserContext> userContextMockedStatic = mockStatic(UserContext.class)) {
            UserDto mockUserDto = new UserDto(1L, "홍길동");
            userContextMockedStatic.when(UserContext::get).thenReturn(mockUserDto);

            // when & then
            mockMvc.perform(delete("/")
                            .header("X-User-Id", "1")
                            .header("X-User-Name", "홍길동"))

                    .andExpect(status().isOk())
                    .andDo(document("user-delete",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("User")
                                    .summary("회원 탈퇴 API")
                                    .description("현재 로그인한 사용자를 삭제(탈퇴)합니다.")
                                    .requestHeaders(
                                            headerWithName("X-User-Id").description("Gateway에서 주입하는 인증된 사용자 ID"),
                                            headerWithName("X-User-Name").description("Gateway에서 주입하는 인증된 사용자 이름 (URL 인코딩됨)")
                                    )
                                    .responseFields(
                                            fieldWithPath("resultMessage").description("결과 메시지"),
                                            fieldWithPath("resultData").description("결과 데이터 (null)").type(JsonFieldType.NULL).optional()
                                    )
                                    .build()
                            )));
        }
    }

    @Test
    @DisplayName("로그아웃 API 명세화")
    void signOut() throws Exception {
        // given
        doNothing().when(jwtTokenManager).signOut(any());

        // when & then
        mockMvc.perform(post("/signout"))
                .andExpect(status().isOk())
                .andDo(document("user-signout",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Sign")
                                .summary("로그아웃 API")
                                .description("인증 쿠키 토큰을 만료시킵니다.")
                                .responseFields(
                                        fieldWithPath("resultMessage").description("결과 메시지"),
                                        fieldWithPath("resultData").description("결과 데이터 (1: 성공)")
                                )
                                .build()
                        )));
    }

    @Test
    @DisplayName("토큰 재발행 API 명세화")
    void reissue() throws Exception {
        // given
        doNothing().when(jwtTokenManager).reissue(any(), any());

        // when & then
        mockMvc.perform(post("/reissue"))
                .andExpect(status().isOk())
                .andDo(document("user-reissue",
                        resource(ResourceSnippetParameters.builder()
                                .tag("User")
                                .summary("토큰 재발행 API")
                                .description("Refresh 토큰을 이용하여 Access 토큰을 재발급합니다.")
                                .responseFields(
                                        fieldWithPath("resultMessage").description("결과 메시지"),
                                        fieldWithPath("resultData").description("결과 데이터 (null)").type(JsonFieldType.NULL).optional()
                                )
                                .build()
                        )));
    }
}