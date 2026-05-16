package com.project.devlog.global.response.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResponse {
    private final HttpStatus status;
    private final String message;

    @Builder
    public ErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
