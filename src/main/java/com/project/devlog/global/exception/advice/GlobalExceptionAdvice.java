package com.project.devlog.global.exception.advice;

import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.errorcode.CommonErrorCode;
import com.project.devlog.global.response.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity<Object> BusinessException(final BusinessException e) {
        log.error("[BusinessException] {} - {}", e.getHttpStatus().value(), e.getMessage());
        return ResponseEntity.status(e.getHttpStatus())
                .body(new ErrorResponse(e.getHttpStatus(), e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Object> exception(final Exception e) {
        HttpStatus errorStatus = CommonErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus();
        log.error("[{}] {}", e.getClass().getName(), e.getMessage());
        return ResponseEntity.status(errorStatus)
                .body(new ErrorResponse(errorStatus, e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleValidationException(final MethodArgumentNotValidException e) {
        HttpStatus errorStatus = HttpStatus.BAD_REQUEST;
        String errorMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        log.error("[MethodArgumentNotValidException] {}", errorMessage);
        return ResponseEntity.status(errorStatus)
                .body(new ErrorResponse(errorStatus, errorMessage));
    }

    @ExceptionHandler
    public ResponseEntity<Object> AccessDeniedException(final AccessDeniedException e) {
        HttpStatus errorStatus = HttpStatus.FORBIDDEN;
        log.error("[AccessDeniedException] {} - {}", e.getClass().getName(), e.getMessage());
        return ResponseEntity.status(errorStatus)
                .body(new ErrorResponse(errorStatus, "접근 권한이 없습니다."));
    }
}
