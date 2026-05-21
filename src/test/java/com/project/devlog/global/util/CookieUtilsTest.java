package com.project.devlog.global.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class CookieUtilsTest {

    private CookieUtils sut;
    private final int MOCK_EXPIRATION_TIME = 604800;

    @BeforeEach
    void setUp() {
        sut = new CookieUtils();
        ReflectionTestUtils.setField(sut, "refreshExpirationTime", MOCK_EXPIRATION_TIME);
    }

    @Nested
    @DisplayName("리프레시 토큰 쿠키 생성 테스트")
    class CreateRefreshTokenCookie {
        @Test
        @DisplayName("성공: 토큰 값과 보안 설정이 올바르게 세팅된 쿠키를 생성한다")
        void success() throws Exception {
            // given
            String token = "sample.refresh.token";

            // when
            Cookie cookie = sut.createRefreshTokenCookie(token);

            // then
            assertAll(
                    () -> assertThat(cookie.getName()).isEqualTo("refresh_token"),
                    () -> assertThat(cookie.getValue()).isEqualTo(token),
                    () -> assertThat(cookie.getMaxAge()).isEqualTo(MOCK_EXPIRATION_TIME),
                    () -> assertThat(cookie.isHttpOnly()).isTrue(),
                    () -> assertThat(cookie.getSecure()).isTrue(),
                    () -> assertThat(cookie.getPath()).isEqualTo("/")
            );
        }
    }

    @Nested
    @DisplayName("리프레시 토큰 쿠키 삭제 테스트")
    class DeleteRefreshCookie {

        @Test
        @DisplayName("성공: 만료 시간이 0이고 값이 null인 삭제용 쿠키를 반환한다")
        void success() {
            // when
            Cookie cookie = sut.deleteRefreshCookie();

            // then
            assertAll(
                    () -> assertThat(cookie.getName()).isEqualTo("refresh_token"),
                    () -> assertThat(cookie.getValue()).isNull(),
                    () -> assertThat(cookie.getMaxAge()).isZero(),
                    () -> assertThat(cookie.isHttpOnly()).isTrue(),
                    () -> assertThat(cookie.getSecure()).isTrue(),
                    () -> assertThat(cookie.getPath()).isEqualTo("/")
            );
        }
    }

    @Nested
    @DisplayName("리프레시 토큰 추출 테스트")
    class ExtractRefreshToken {

        private final HttpServletRequest request = mock(HttpServletRequest.class);

        @Test
        @DisplayName("성공: 요청 헤더에 refresh_token 쿠키가 존재하면 값을 정확히 추출한다")
        void success() {
            // given
            Cookie[] cookies = {
                    new Cookie("refresh_token", "target_token_value"),
                    new Cookie("other_cookie", "bbb")
            };
            given(request.getCookies()).willReturn(cookies);

            // when
            String result = sut.extractRefreshToken(request);

            // then
            assertThat(result).isEqualTo("target_token_value");
        }

        @Test
        @DisplayName("방어 코드: 요청에 쿠키가 아예 존재하지 않는(null) 경우 null을 반환한다")
        void fail_cookiesIsNull() {
            // given
            given(request.getCookies()).willReturn(null);

            // when
            String result = sut.extractRefreshToken(request);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("방어 코드: 쿠키 배열은 존재하지만 refresh_token이라는 이름을 가진 쿠키가 없다면 null을 반환한다")
        void fail_noTargetCookie() {
            // given
            Cookie[] cookies = {
                    new Cookie("access_token", "aaa"),
                    new Cookie("some_other_cookie", "bbb")
            };
            given(request.getCookies()).willReturn(cookies);

            // when
            String result = sut.extractRefreshToken(request);

            // then
            assertThat(result).isNull();
        }
    }
}