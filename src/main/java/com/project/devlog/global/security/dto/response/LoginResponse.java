package com.project.devlog.global.security.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(
        Long userId,
        String email,
        String name
) {
}
