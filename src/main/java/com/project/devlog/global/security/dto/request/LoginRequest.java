package com.project.devlog.global.security.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
