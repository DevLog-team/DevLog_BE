package com.project.devlog.global.security.jwt;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.project.devlog.domain.user.entity.enums.UserRole;
import io.jsonwebtoken.Claims;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class JwtProviderTest {

    JwtProvider sut;

    JwtProperties jwtProperties;


    @BeforeEach
    void setUp() throws Exception {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecretKey("YmFzZTY0ZW5jb2RlZHNlY3JldA==asdfzxcvawsdfqwersadfzxcvasdf");
        jwtProperties.setAccessExpirationTime(1000 * 60);
        jwtProperties.setRefreshExpirationTime(1000 * 60 * 60);
        jwtProperties.setAuthoritiesKey("roles");

        sut = new JwtProvider(jwtProperties);
    }

    @Test
    @DisplayName("AccessToken 생성 테스트: 성공")
    void generate_access_token_test() throws Exception {
        // given
        String email = "test@naver.com";
        Long userId = 1L;
        Collection<? extends GrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority(UserRole.USER.name()));

        // when
        String result = sut.generateAccessToken(email, userId, authorities);
        Claims payload = sut.getClaims(result);

        // then
        assertAll(
                () -> assertThat(payload.getSubject()).isEqualTo(email),
                () -> assertThat(payload.get("id", Long.class)).isEqualTo(userId),
                () -> assertThat(payload.get("roles", List.class).get(0)).isEqualTo(UserRole.USER.name())
        );
    }
}