package com.project.devlog.global.security.handler;

import com.project.devlog.domain.auth.service.AuthService;
import com.project.devlog.global.security.dto.response.LoginResponse;
import com.project.devlog.global.security.jwt.JwtProvider;
import com.project.devlog.global.security.vo.CustomUserDetails;
import com.project.devlog.global.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessCustomHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final ResponseUtil responseUtil;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        String email = userDetails.getEmail();

        String accessToken = jwtProvider.generateAccessToken(email, userId, userDetails.getAuthorities());
        String refreshToken = jwtProvider.generateRefreshToken(email, userId);

        authService.registerRefreshToken(userId, refreshToken);

        LoginResponse body = buildBody(userDetails);

        responseUtil.addTokensToResponse(response, accessToken, refreshToken);
        responseUtil.writeJsonSuccessResponse(response, body);
    }

    private LoginResponse buildBody(CustomUserDetails userDetails) {
        return LoginResponse.builder()
                .userId(userDetails.getUserId())
                .email(userDetails.getEmail())
                .name(userDetails.getName())
                .build();
    }
}
