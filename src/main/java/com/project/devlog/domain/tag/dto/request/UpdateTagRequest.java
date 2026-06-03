package com.project.devlog.domain.tag.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateTagRequest(
        @NotBlank String name,
        @NotBlank String color
) {
}
