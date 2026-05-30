package com.project.devlog.global.exception.errorcode;

import com.project.devlog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ProjectErrorCode  implements ErrorCode {

    INVALID_PROJECT_DATE(HttpStatus.BAD_REQUEST, "프로젝트 마감일은 시작일보다 빠를 수 없습니다."),
    INVALID_PROJECT_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 프로젝트 상태 값입니다."),
    PROJECT_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 프로젝트를 찾을 수 없거나 접근 권한이 없습니다."),
    PROJECT_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "프로젝트 시작일과 마감일은 필수 항목입니다.");

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
