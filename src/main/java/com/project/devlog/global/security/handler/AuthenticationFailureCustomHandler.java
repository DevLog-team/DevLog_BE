package com.project.devlog.global.security.handler;

import com.project.devlog.global.exception.errorcode.AuthErrorCode;
import com.project.devlog.global.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationFailureCustomHandler implements AuthenticationFailureHandler {

    private final ResponseUtil responseUtil;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        responseUtil.writeJsonErrorResponse(response, AuthErrorCode.AUTHENTICATION_FAILED);
    }
}
