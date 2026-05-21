package com.project.devlog.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.errorcode.CommonErrorCode;
import com.project.devlog.global.security.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            LoginRequest loginDto = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            UsernamePasswordAuthenticationToken authenticationToken = createAuthenticationToken(loginDto);
            return getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            throw new BusinessException(CommonErrorCode.DATA_IO_ERROR);
        }
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(LoginRequest loginDto) {
        String email = loginDto.email();
        String password = loginDto.password();
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
