package com.project.devlog.global.response.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
public class ApiResponse<T> {
    private ResponseStatus status;
    private T body;
    private LocalDateTime timestamp = LocalDateTime.now();

    @Builder
    public ApiResponse(ResponseStatus status, T body) {
        this.status = status;
        this.body = body;
    }
}
