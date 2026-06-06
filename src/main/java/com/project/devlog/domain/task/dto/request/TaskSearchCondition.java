package com.project.devlog.domain.task.dto.request;

public record TaskSearchCondition(
        String title,
        Long assigneeId,
        String status,
        String priority
) {
}
