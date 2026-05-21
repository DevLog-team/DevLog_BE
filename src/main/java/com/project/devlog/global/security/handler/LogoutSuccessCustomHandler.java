package com.project.devlog.global.security.handler;

import com.project.devlog.domain.auth.service.AuthService;
import com.project.devlog.global.security.jwt.JwtProvider;
import com.project.devlog.global.util.CookieUtils;
import com.project.devlog.global.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class LogoutSuccessCustomHandler implements LogoutSuccessHandler {

    private final AuthService authService;
    private final ResponseUtil responseUtil;
    private final CookieUtils cookieUtils;
    private final JwtProvider jwtProvider;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        deleteRefreshCookie(request, response);
        responseUtil.writeJsonSuccessResponse(response);
    }

    private void deleteRefreshCookie(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtils.extractRefreshToken(request);

        if (!StringUtils.hasText(refreshToken)) {
            return;
        }

        Long userId = jwtProvider.getClaims(refreshToken).get("id", Long.class);

        authService.deleteRefreshToken(userId);
        response.addCookie(cookieUtils.deleteRefreshCookie());
    }
}
