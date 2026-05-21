package com.project.devlog.global.security.filter;

import com.project.devlog.global.exception.errorcode.AuthErrorCode;
import com.project.devlog.global.security.jwt.JwtProperties;
import com.project.devlog.global.security.jwt.JwtProvider;
import com.project.devlog.global.security.vo.AttributeKey;
import com.project.devlog.global.security.vo.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtVerficationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            setAuthenticationContext(request);
        } catch (ExpiredJwtException ee) {
            request.setAttribute(AttributeKey.ERROR_CODE_ATTRIBUTE.name(), AuthErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (SignatureException se) {
            request.setAttribute(AttributeKey.ERROR_CODE_ATTRIBUTE.name(), AuthErrorCode.INVALID_SIGNATURE_ACCESS_TOKEN);
        } catch (JwtException je) {
            request.setAttribute(AttributeKey.ERROR_CODE_ATTRIBUTE.name(), AuthErrorCode.UNAUTHENTICATED);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !validAuthorizationHeader(request);
    }

    private boolean validAuthorizationHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        return authorizationHeader != null && authorizationHeader.startsWith(jwtProperties.getTokenPrefix());
    }

    private void setAuthenticationContext(HttpServletRequest request) {
        SecurityContextHolder.getContext().setAuthentication(createAuthenticatedToken(request));
    }

    private Authentication createAuthenticatedToken(HttpServletRequest request) {
        CustomUserDetails userDetails = createUserDetails(request);
        return new UsernamePasswordAuthenticationToken(userDetails.getUserId(), null, userDetails.getAuthorities());
    }

    private CustomUserDetails createUserDetails(HttpServletRequest request) {
        String token = getAuthenticationToken(request);
        return new CustomUserDetails(jwtProvider.getClaims(token));
    }

    private String getAuthenticationToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return header != null ? header.substring(jwtProperties.getTokenPrefix().length()) : null;
    }
}
