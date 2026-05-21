package com.project.devlog.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.ErrorCode;
import com.project.devlog.global.exception.errorcode.CommonErrorCode;
import com.project.devlog.global.response.dto.ApiResponse;
import com.project.devlog.global.response.dto.ErrorResponse;
import com.project.devlog.global.response.dto.ResponseStatus;
import com.project.devlog.global.security.jwt.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseUtil {

    private final CookieUtils cookieUtils;
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    public void writeJsonSuccessResponse(HttpServletResponse response) {
        ApiResponse<Void> body = createSuccessBody();
        setResponseHeader(response, HttpStatus.OK);
        writeResponseBody(response, body);
    }

    private ApiResponse<Void> createSuccessBody() {
        return ApiResponse.<Void>builder()
                .status(ResponseStatus.SUCCESS)
                .build();
    }

    public void writeJsonErrorResponse(HttpServletResponse response, ErrorCode errorCode) {
        ApiResponse<ErrorResponse> body = createErrorBody(errorCode);
        setResponseHeader(response, errorCode.getHttpStatus());
        writeResponseBody(response, body);
    }

    private ApiResponse<ErrorResponse> createErrorBody(ErrorCode errorCode) {
        ErrorResponse errorBody = ErrorResponse.builder()
                .status(errorCode.getHttpStatus())
                .message(errorCode.getMessage())
                .build();

        return ApiResponse.<ErrorResponse>builder()
                .status(ResponseStatus.ERROR)
                .body(errorBody)
                .build();
    }

    private void setResponseHeader(HttpServletResponse response, HttpStatus status) {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
    }

    private <T> void writeResponseBody(HttpServletResponse response, ApiResponse<T> body) {
        try (OutputStream out = response.getOutputStream()) {
            objectMapper.writeValue(out, body);
        } catch (IOException e) {
            throw new BusinessException(CommonErrorCode.DATA_IO_ERROR);
        }
    }

    public void addTokensToResponse(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie refreshTokenCookie = cookieUtils.createRefreshTokenCookie(refreshToken);
        response.addCookie(refreshTokenCookie);
        response.addHeader(HttpHeaders.AUTHORIZATION, jwtProperties.getTokenPrefix() + accessToken);
    }
}
