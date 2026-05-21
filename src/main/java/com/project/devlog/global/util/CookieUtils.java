package com.project.devlog.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    @Value("${jwt.refresh-expiration-time}")
    private int refreshExpirationTime;
    private final String refreshCookieName = "refresh_token";

    public Cookie createRefreshTokenCookie(String token) {
        Cookie cookie = new Cookie(refreshCookieName, token);
        cookie.setMaxAge(refreshExpirationTime);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        return cookie;
    }

    public Cookie deleteRefreshCookie() {
        Cookie cookie = new Cookie(refreshCookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    public String extractRefreshToken(HttpServletRequest request) {
        return extractCookieValue(request, refreshCookieName);
    }

    private String extractCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
