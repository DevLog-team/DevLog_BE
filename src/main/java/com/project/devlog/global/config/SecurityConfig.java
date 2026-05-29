package com.project.devlog.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.devlog.domain.user.entity.enums.UserRole;
import com.project.devlog.global.security.filter.JwtAuthenticationFilter;
import com.project.devlog.global.security.filter.JwtVerficationFilter;
import com.project.devlog.global.security.handler.AccessDeniedCustomHandler;
import com.project.devlog.global.security.handler.AuthenticationEntryPointCustom;
import com.project.devlog.global.security.handler.AuthenticationFailureCustomHandler;
import com.project.devlog.global.security.handler.AuthenticationSuccessCustomHandler;
import com.project.devlog.global.security.handler.LogoutSuccessCustomHandler;
import com.project.devlog.global.security.jwt.JwtProperties;
import com.project.devlog.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationSuccessCustomHandler successHandler;
    private final AuthenticationFailureCustomHandler failureHandler;
    private final AuthenticationEntryPointCustom authenticationEntryPoint;
    private final AccessDeniedCustomHandler accessDeniedHandler;
    private final LogoutSuccessCustomHandler logoutSuccessHandler;
    private final ObjectMapper objectMapper;

    private static final String LOGIN_URL = "/api/login";
    private static final String LOGOUT_URL = "/api/logout";

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/signup",
            "/api/reissue",
            "swagger-ui/**",
            "/webjars/**",
    };

    private static final String[] ADMIN_ENDPOINTS = {
    };

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager,
                                                   JwtProperties jwtProperties,
                                                   JwtProvider jwtProvider) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole(UserRole.ADMIN.name())
                        .anyRequest().authenticated());

        http
                .addFilterBefore(new JwtVerficationFilter(jwtProperties, jwtProvider), JwtAuthenticationFilter.class)
                .addFilterAt(jwtAuthenticationFilter(authenticationManager),
                        UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout.logoutSuccessHandler(logoutSuccessHandler).logoutUrl(LOGOUT_URL))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(objectMapper);
        filter.setFilterProcessesUrl(LOGIN_URL);
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);
        return filter;
    }
}
