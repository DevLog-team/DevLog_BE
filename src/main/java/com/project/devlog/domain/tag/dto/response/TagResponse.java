package com.project.devlog.domain.tag.dto.response;

public record TagResponse(
        Long tagId,
        String name,
        String color
) {
}
