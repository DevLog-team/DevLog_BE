package com.project.devlog.domain.task.dto.response;

import lombok.Builder;

@Builder
public record TagResponse(
        Long tagId,
        String name,
        String color
) {
}
