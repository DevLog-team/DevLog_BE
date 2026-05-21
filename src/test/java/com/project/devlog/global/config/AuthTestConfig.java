package com.project.devlog.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.devlog.domain.auth.service.AuthService;
import com.project.devlog.domain.user.controller.mapper.UserMapper;
import com.project.devlog.domain.user.mock.UserMock;
import com.project.devlog.domain.user.repository.UserRepository;
import com.project.devlog.domain.user.service.UserService;
import com.project.devlog.global.cache.RedisRepository;
import com.project.devlog.global.security.handler.AccessDeniedCustomHandler;
import com.project.devlog.global.security.handler.AuthenticationEntryPointCustom;
import com.project.devlog.global.security.handler.AuthenticationFailureCustomHandler;
import com.project.devlog.global.security.handler.AuthenticationSuccessCustomHandler;
import com.project.devlog.global.security.handler.LogoutSuccessCustomHandler;
import com.project.devlog.global.security.jwt.JwtProperties;
import com.project.devlog.global.security.jwt.JwtProvider;
import com.project.devlog.global.util.CookieUtils;
import com.project.devlog.global.util.ResponseUtil;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class AuthTestConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public JwtProperties jwtProperties() {
        return new JwtProperties();
    }

    @Bean
    public JwtProvider jwtProvider() {
        return new JwtProvider(jwtProperties());
    }

    @Bean
    public CookieUtils cookieUtils() {
        return new CookieUtils();
    }

    @Bean
    public ResponseUtil responseUtil() {
        return new ResponseUtil(cookieUtils(), jwtProperties(), objectMapper());
    }

    @Bean
    public AuthenticationSuccessCustomHandler authenticationSuccessCustomHandler() {
        return new AuthenticationSuccessCustomHandler(authService(), jwtProvider(), responseUtil());
    }

    @Bean
    public AuthenticationFailureCustomHandler authenticationFailureCustomHandler() {
        return new AuthenticationFailureCustomHandler(responseUtil());
    }

    @Bean
    public AuthenticationEntryPointCustom authenticationEntryPointCustom() {
        return new AuthenticationEntryPointCustom(responseUtil());
    }

    @Bean
    public AccessDeniedCustomHandler accessDeniedCustomHandler() {
        return new AccessDeniedCustomHandler(responseUtil());
    }

    @Bean
    public LogoutSuccessCustomHandler logoutSuccessCustomHandler() {
        return new LogoutSuccessCustomHandler(authService(), responseUtil(), cookieUtils(), jwtProvider());
    }

    @Bean
    public AuthService authService() {
        return new AuthService(
                userRepository(),
                redisRepository(),
                responseUtil(),
                cookieUtils(),
                jwtProperties(),
                jwtProvider()
        );
    }

    @Bean
    public RedisRepository redisRepository() { return Mockito.mock(RedisRepository.class); }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }

    @Bean
    public UserMock userMock() {
        return new UserMock(passwordEncoder());
    }
}
