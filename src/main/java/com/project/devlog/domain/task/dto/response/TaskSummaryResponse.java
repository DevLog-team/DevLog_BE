package com.project.devlog.domain.task.dto.response;

import com.project.devlog.domain.task.entity.enums.TaskPriority;
import com.project.devlog.domain.task.entity.enums.TaskStatus;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TaskSummaryResponse(
        Long taskId,
        String title,
        String assigneeName,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate
) {
}
