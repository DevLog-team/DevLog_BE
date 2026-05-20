package com.project.devlog.global.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException{

    private final ErrorCode errorCode;

    public BusinessException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() { return errorCode.getHttpStatus(); }

    public String getErrorMessage() { return errorCode.getMessage(); }
}
