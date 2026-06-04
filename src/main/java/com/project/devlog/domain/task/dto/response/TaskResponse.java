package com.project.devlog.domain.task.dto.response;

import com.project.devlog.domain.task.entity.enums.TaskPriority;
import com.project.devlog.domain.task.entity.enums.TaskStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record TaskResponse(
        Long taskId,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        String assigneeName,
        List<TagResponse> tags
) {
}
