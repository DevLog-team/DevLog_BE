package com.project.devlog.domain.task.dto.response;

import lombok.Builder;

@Builder
public record TagResponse(
        Long id,
        String name,
        String color
) {
}
