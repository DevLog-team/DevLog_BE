package com.project.devlog.domain.task.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateTaskRequest(
        @NotBlank String title,
        String description
) {
}
