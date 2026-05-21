package com.project.devlog.global.security.dto;

public record LoginRequest(
        String email,
        String password
) {
}
