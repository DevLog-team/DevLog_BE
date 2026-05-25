package com.project.devlog.domain.auth.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.devlog.domain.user.entity.User;
import com.project.devlog.domain.user.mock.UserMock;
import com.project.devlog.domain.user.repository.UserRepository;
import com.project.devlog.global.cache.RedisRepository;
import com.project.devlog.global.config.AuthTestConfig;
import com.project.devlog.global.config.SecurityConfig;
import com.project.devlog.global.exception.errorcode.AuthErrorCode;
import com.project.devlog.global.response.dto.ResponseStatus;
import com.project.devlog.global.security.dto.request.LoginRequest;
import com.project.devlog.global.security.jwt.JwtProperties;
import com.project.devlog.global.security.jwt.JwtProvider;
import com.project.devlog.global.util.CookieUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(AuthController.class)
@Import({AuthTestConfig.class, SecurityConfig.class, UserMock.class})
@AutoConfigureRestDocs
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RedisRepository redisRepository;

    @Autowired
    CookieUtils cookieUtils;

    @Autowired
    UserMock userMock;

    @Nested
    @DisplayName("로그인 테스트")
    class login {
        @Test
        @DisplayName("성공: Header, Cookie 각각에 Access, Refresh 토큰 담아서 반환한다.")
        void success() throws Exception {
            // given
            LoginRequest requestDto = userMock.loginMock();
            User mockUser = userMock.domainMock();
            String content = objectMapper.writeValueAsString(requestDto);

            given(userRepository.findByEmailAndIsDeletedFalse(anyString()))
                    .willReturn(Optional.of(mockUser));

            // when
            ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(content));

            // then
            String authorizationHeader = perform
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.body.userId").isNumber())
                    .andExpect(jsonPath("$.body.email").isString())
                    .andExpect(jsonPath("$.body.name").isString())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andDo(document("로그인 성공",
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Auth")
                                                    .description("로그인 API")
                                                    .requestSchema(Schema.schema("LoginRequest"))
                                                    .requestFields(
                                                            fieldWithPath("email").type(JsonFieldType.STRING)
                                                                    .description("사용자 이메일"),
                                                            fieldWithPath("password").type(JsonFieldType.STRING)
                                                                    .description("사용자 비밀번호"))
                                                    .responseSchema(Schema.schema("LoginResponse"))
                                                    .responseFields(
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("응답 상태"),
                                                            fieldWithPath("body.userId").type(JsonFieldType.NUMBER)
                                                                    .description("사용자 ID"),
                                                            fieldWithPath("body.email").type(JsonFieldType.STRING)
                                                                    .description("사용자 이메일"),
                                                            fieldWithPath("body.name").type(JsonFieldType.STRING)
                                                                    .description("사용자 이름"),
                                                            fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                                    .description("응답 시간"))
                                                    .responseHeaders(
                                                            headerWithName(HttpHeaders.AUTHORIZATION)
                                                                    .description("엑세스 토큰을 Header Authorization에 담아서 응답"),
                                                            headerWithName(HttpHeaders.SET_COOKIE)
                                                                    .description("리프레시 토큰을 HttpOnly 쿠키에 담아서 응답")
                                                    )
                                                    .build()
                                    )
                            )
                    )
                    .andReturn()
                    .getResponse()
                    .getHeader(HttpHeaders.AUTHORIZATION);

            assertThat(authorizationHeader).startsWith(jwtProperties.getTokenPrefix());
        }

        @Nested
        @DisplayName("로그아웃 테스트")
        class logout {
            @Test
            @DisplayName("성공: Cookie Refresh 토큰 제거")
            void success() throws Exception {
                // given
                User mockUser = userMock.domainMock();
                String preRefreshToken = jwtProvider.generateRefreshToken(mockUser.getEmail(), mockUser.getId());

                // when
                ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/logout")
                        .cookie(createCookie(preRefreshToken)));

                // then
                perform
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").isString())
                        .andExpect(jsonPath("$.timestamp").isString())
                        .andDo(document("로그아웃 성공",
                                        resource(
                                                ResourceSnippetParameters.builder()
                                                        .tag("Auth")
                                                        .description("로그아웃 API")
                                                        .responseFields(
                                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                                        .description("응답 상태"),
                                                                fieldWithPath("body").type(null)
                                                                        .description("응답 데이타"),
                                                                fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                                        .description("응답 시간"))
                                                        .build()
                                        )
                                )
                        );
            }
        }

        @Test
        @DisplayName("실패: 아이디, 비밀번호가 올바르지 않을 경우")
        void fail() throws Exception {
            // given
            LoginRequest loginRequest = userMock.wrongLoginMock();
            User mockUser = userMock.domainMock();
            String content = objectMapper.writeValueAsString(loginRequest);

            given(userRepository.findByEmailAndIsDeletedFalse(anyString()))
                    .willReturn(Optional.of(mockUser));

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(content));

            // then
            perform
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.body.status").isString())
                    .andExpect(jsonPath("$.body.message").isString())
                    .andExpect(jsonPath("$.timestamp").isString());
        }
    }

    @Nested
    @DisplayName("리프레쉬 토큰 재발급 테스트")
    class reissue {
        @Test
        @DisplayName("성공: 새로 발급된 accessToken, refreshToken 반환")
        void success() throws Exception {
            User mockUser = userMock.domainMock();
            String preRefreshToken = jwtProvider.generateRefreshToken(mockUser.getEmail(), mockUser.getId());

            given(userRepository.findByEmailAndIsDeletedFalse(anyString()))
                    .willReturn(Optional.of(mockUser));

            given(redisRepository.findByKey(anyString())).willReturn(preRefreshToken);

            willDoNothing().given(redisRepository).save(anyString(), anyString(), anyInt(), any(TimeUnit.class));

            // when
            ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/reissue")
                    .cookie(createCookie(preRefreshToken)));

            // then
            perform
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.cookie().exists("refresh_token"))
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andDo(document("토큰 재발급 성공",
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Auth")
                                                    .description("리프레시 토큰 재발급 API")
                                                    .responseFields(
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("응답 상태"),
                                                            fieldWithPath("body").type(null)
                                                                    .description("응답 데이타"),
                                                            fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                                    .description("응답 시간"))
                                                    .responseHeaders(
                                                            headerWithName(HttpHeaders.AUTHORIZATION)
                                                                    .description("엑세스 토큰을 Header Authorization에 담아서 응답"),
                                                            headerWithName(HttpHeaders.SET_COOKIE)
                                                                    .description("리프레시 토큰을 HttpOnly 쿠키에 담아서 응답")
                                                    )
                                                    .build()
                                    )
                            )
                    );

        }

        @Test
        @DisplayName("실패: 쿠키에 refresh_token 없을 경우")
        void failure_when_cookie_not_exist() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/reissue")
            );

            // then
            perform
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.body.status").isString())
                    .andExpect(jsonPath("$.body.message").value(AuthErrorCode.INVALID_REFRESH_TOKEN.getMessage()))
                    .andExpect(jsonPath("$.timestamp").isString());
        }

        @Test
        @DisplayName("실패: 저장된 refresh_token 데이터가 없을 경우")
        void failure_when_refresh_token_not_exist() throws Exception {
            // given
            User mockUser = userMock.domainMock();
            String preRefreshToken = jwtProvider.generateRefreshToken(mockUser.getEmail(), mockUser.getId());

            given(userRepository.findByEmailAndIsDeletedFalse(anyString()))
                    .willReturn(Optional.of(mockUser));

            given(redisRepository.findByKey(anyString())).willReturn(null);

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/reissue")
                    .cookie(createCookie(preRefreshToken)));

            // then
            perform
                    .andExpect(status().isUnauthorized())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(ResponseStatus.ERROR.name()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.body.message")
                            .value(AuthErrorCode.INVALID_REFRESH_TOKEN.getMessage()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").isString());
        }

        @Test
        @DisplayName("실패: 토큰이 만료되었을 경우")
        void failure_when_token_expire() throws Exception {
            // given
            String refrshToken = generateRefrshToken("test@naver.com", 1L,
                    -1, "");

            // when
            ResultActions perform = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/reissue")
                            .cookie(createCookie(refrshToken)));

            // then
            perform
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.body.status").isString())
                    .andExpect(jsonPath("$.body.message").value(AuthErrorCode.REFRESH_TOKEN_EXPIRED.getMessage()))
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andDo(document("토큰 재발급 실패 - 리프레시 토큰이 만료 되었을 경우",
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Auth")
                                                    .description("리프레시 토큰 재발급 API")
                                                    .responseFields(
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("응답 상태"),
                                                            fieldWithPath("body.status").type(JsonFieldType.STRING)
                                                                    .description("에러 상태"),
                                                            fieldWithPath("body.message").type(JsonFieldType.STRING)
                                                                    .description("에러 메시지"),
                                                            fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                                    .description("응답 시간"))
                                                    .build()
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("실패: 토큰 시그니처가 다를 경우")
        void failure_when_token_signature_error() throws Exception {
            // given
            String refrshToken = generateRefrshToken("test@naver.com", 1L,
                    20000, "error");

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/reissue")
                    .cookie(createCookie(refrshToken)));

            // then
            perform.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                    .andExpect(
                            MockMvcResultMatchers.jsonPath("$.body.message")
                                    .value(AuthErrorCode.INVALID_SIGNATURE_ACCESS_TOKEN.getMessage()));
        }
    }

    private Cookie createCookie(String preRefreshToken) {
        return cookieUtils.createRefreshTokenCookie(preRefreshToken);
    }

    private String generateRefrshToken(String subject, Long id, int expire, String key) {
        long now = new Date().getTime();

        return Jwts.builder()
                .subject(subject)
                .claim("id", id)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expire))
                .signWith(getEncodedKey(key))
                .compact();
    }

    private SecretKey getEncodedKey(String parameter) {
        byte[] byteSecretKey = Decoders.BASE64.decode(jwtProperties.getSecretKey() + parameter);
        return Keys.hmacShaKeyFor(byteSecretKey);
    }
}