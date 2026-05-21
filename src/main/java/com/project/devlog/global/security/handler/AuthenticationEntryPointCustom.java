package com.project.devlog.global.security.handler;

import com.project.devlog.global.exception.errorcode.AuthErrorCode;
import com.project.devlog.global.security.vo.AttributeKey;
import com.project.devlog.global.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointCustom implements AuthenticationEntryPoint {

    private final ResponseUtil responseUtil;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        AuthErrorCode errorCode = AuthErrorCode.UNAUTHENTICATED;
        Object attribute = request.getAttribute(AttributeKey.ERROR_CODE_ATTRIBUTE.name());
        if (attribute instanceof AuthErrorCode) {
            errorCode = (AuthErrorCode) attribute;
        }
        responseUtil.writeJsonErrorResponse(response, errorCode);
    }
}
