package com.project.devlog.global.exception.errorcode;

import com.project.devlog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TaskErrorCode  implements ErrorCode {

    TASK_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 작업을 찾을 수 없습니다.");

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
