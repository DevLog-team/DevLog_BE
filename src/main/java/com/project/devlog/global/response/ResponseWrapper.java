package com.project.devlog.global.response;

import com.project.devlog.global.response.dto.ApiResponse;
import com.project.devlog.global.response.dto.ErrorResponse;
import com.project.devlog.global.response.dto.ResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@RestControllerAdvice
public class ResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        log.info("execute AOP - supports");
        log.info("execute AOP - returnType :: {}", returnType);
        log.info("execute AOP - converterType :: {}", converterType);
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        log.info("execute AOP - beforeBodyWrite");

        String path = request.getURI().getPath();
        if (path.startsWith("/swagger-ui")) {
            return body;
        }

        if (body instanceof ErrorResponse) {
            return new ApiResponse<>(ResponseStatus.ERROR,body);
        }

        return new ApiResponse<>(ResponseStatus.SUCCESS,body);
    }
}
