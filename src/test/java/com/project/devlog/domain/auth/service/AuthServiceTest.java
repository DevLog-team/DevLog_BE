package com.project.devlog.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.project.devlog.domain.user.entity.User;
import com.project.devlog.domain.user.entity.enums.UserRole;
import com.project.devlog.domain.user.repository.UserRepository;
import com.project.devlog.global.cache.RedisKeyGenerator;
import com.project.devlog.global.cache.RedisRepository;
import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.errorcode.AuthErrorCode;
import com.project.devlog.global.security.jwt.JwtProperties;
import com.project.devlog.global.security.jwt.JwtProvider;
import com.project.devlog.global.util.CookieUtils;
import com.project.devlog.global.util.ResponseUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService sut;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RedisRepository redisRepository;
    @Mock
    private ResponseUtil responseUtil;
    @Mock
    private CookieUtils cookieUtils;
    @Mock
    private JwtProperties jwtProperties;
    @Mock
    private JwtProvider jwtProvider;

    private User createMockUser(Long id, String email, UserRole role) {
        return User.builder()
                .id(id)
                .email(email)
                .password("encodedPassword123!")
                .role(role)
                .build();
    }


    @Nested
    @DisplayName("reissue (토큰 재발급) 테스트")
    class Reissue {

        private final HttpServletRequest request = mock(HttpServletRequest.class);
        private final HttpServletResponse response = mock(HttpServletResponse.class);
        private final String preToken = "old.refresh.token";
        private final String email = "test@naver.com";
        private final Long userId = 1L;

        @Test
        @DisplayName("성공: 모든 검증을 통과하면 새로운 Access/Refresh 토큰쌍을 응답에 추가하고 Redis를 갱신한다")
        void success() {
            // given
            User user = createMockUser(userId, email, UserRole.USER);
            Claims mockClaims = mock(Claims.class);
            String newAccess = "new.access.token";
            String newRefresh = "new.refresh.token";

            given(cookieUtils.extractRefreshToken(request)).willReturn(preToken);
            given(jwtProvider.getClaims(preToken)).willReturn(mockClaims);
            given(mockClaims.getSubject()).willReturn(email);
            given(userRepository.findByEmailAndIsDeletedFalse(email)).willReturn(Optional.of(user));
            given(redisRepository.findByKey(RedisKeyGenerator.getRefreshTokenKey(userId))).willReturn(preToken);

            given(jwtProvider.generateAccessToken(eq(email), eq(userId), any())).willReturn(newAccess);
            given(jwtProvider.generateRefreshToken(email, userId)).willReturn(newRefresh);
            given(jwtProperties.getRefreshExpirationTime()).willReturn(3600);

            // when
            sut.reissue(request, response);

            // then
            assertAll(
                    () -> then(redisRepository).should(times(1))
                            .save(RedisKeyGenerator.getRefreshTokenKey(userId), newRefresh, 3600, TimeUnit.SECONDS),
                    () -> then(responseUtil).should(times(1))
                            .addTokensToResponse(response, newAccess, newRefresh)
            );
        }

        @Test
        @DisplayName("실패: 쿠키에 리프레시 토큰이 없으면 INVALID_REFRESH_TOKEN 예외가 발생한다")
        void fail_noCookieToken() {
            // given
            given(cookieUtils.extractRefreshToken(request)).willReturn("");

            // when & then
            assertThatThrownBy(() -> sut.reissue(request, response))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(AuthErrorCode.INVALID_REFRESH_TOKEN.getMessage());
        }

        @Test
        @DisplayName("실패: 토큰이 만료(ExpiredJwtException)되었다면 REFRESH_TOKEN_EXPIRED 예외가 발생한다")
        void fail_expiredToken() {
            // given
            given(cookieUtils.extractRefreshToken(request)).willReturn(preToken);
            given(jwtProvider.getClaims(preToken)).willThrow(new ExpiredJwtException(null, null, "만료됨"));

            // when & then
            assertThatThrownBy(() -> sut.reissue(request, response))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(AuthErrorCode.REFRESH_TOKEN_EXPIRED.getMessage());
        }

        @Test
        @DisplayName("실패: 토큰의 서명이 위조(SignatureException)되었다면 INVALID_SIGNATURE_ACCESS_TOKEN 예외가 발생한다")
        void fail_invalidSignature() {
            // given
            given(cookieUtils.extractRefreshToken(request)).willReturn(preToken);
            given(jwtProvider.getClaims(preToken)).willThrow(new SignatureException("위조됨"));

            // when & then
            assertThatThrownBy(() -> sut.reissue(request, response))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(AuthErrorCode.INVALID_SIGNATURE_ACCESS_TOKEN.getMessage());
        }

        @Test
        @DisplayName("실패: 그 외 일반적인 JWT 파싱 에러(JwtException)라면 UNAUTHENTICATED 예외가 발생한다")
        void fail_jwtException() {
            // given
            given(cookieUtils.extractRefreshToken(request)).willReturn(preToken);
            given(jwtProvider.getClaims(preToken)).willThrow(new JwtException("기타 에러"));

            // when & then
            assertThatThrownBy(() -> sut.reissue(request, response))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(AuthErrorCode.UNAUTHENTICATED.getMessage());
        }

        @Test
        @DisplayName("실패: 탈취 시나리오 - 쿠키의 토큰과 Redis에 보관된 최신 토큰이 다르면 INVALID_REFRESH_TOKEN 예외가 발생한다")
        void fail_tokenStealScenario() {
            // given
            User user = createMockUser(userId, email, UserRole.USER);
            Claims mockClaims = mock(Claims.class);
            String differentTokenInRedis = "attacker.stole.and.refreshed.token";

            given(cookieUtils.extractRefreshToken(request)).willReturn(preToken);
            given(jwtProvider.getClaims(preToken)).willReturn(mockClaims);
            given(mockClaims.getSubject()).willReturn(email);
            given(userRepository.findByEmailAndIsDeletedFalse(email)).willReturn(Optional.of(user));

            given(redisRepository.findByKey(RedisKeyGenerator.getRefreshTokenKey(userId))).willReturn(
                    differentTokenInRedis);

            // when & then
            assertThatThrownBy(() -> sut.reissue(request, response))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(AuthErrorCode.INVALID_REFRESH_TOKEN.getMessage());
        }
    }
}