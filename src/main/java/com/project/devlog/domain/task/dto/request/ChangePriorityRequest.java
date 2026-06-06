package com.project.devlog.domain.task.dto.request;

import com.project.devlog.domain.task.entity.enums.TaskPriority;
import jakarta.validation.constraints.NotNull;

public record ChangePriorityRequest(
        @NotNull TaskPriority priority
) {
}
