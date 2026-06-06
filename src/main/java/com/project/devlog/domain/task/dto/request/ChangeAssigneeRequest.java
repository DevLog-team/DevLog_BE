package com.project.devlog.domain.task.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChangeAssigneeRequest(
        @NotNull Long userId
) {
}
