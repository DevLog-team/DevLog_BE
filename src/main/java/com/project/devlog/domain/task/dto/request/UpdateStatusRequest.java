package com.project.devlog.domain.task.dto.request;

import com.project.devlog.domain.task.entity.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull TaskStatus status
) {
}
