package com.project.devlog.global.exception.errorcode;

import com.project.devlog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TagErrorCode implements ErrorCode {

    DUPLICATE_TAG_NAME(HttpStatus.CONFLICT, "이미 존재하는 태그 이름입니다."),
    TAG_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 태그를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
