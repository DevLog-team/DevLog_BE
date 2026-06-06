package com.project.devlog.domain.task.dto.response;

import com.project.devlog.domain.task.entity.enums.TaskPriority;
import java.util.List;

public record TaskPriorityListResponse(
        List<TaskPriority> taskPriorities
) {
}
