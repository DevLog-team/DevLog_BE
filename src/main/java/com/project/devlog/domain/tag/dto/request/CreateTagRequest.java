package com.project.devlog.domain.tag.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record CreateTagRequest(
        @NotEmpty String name,
        @NotEmpty String color
) {
}
