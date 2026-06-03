package com.project.devlog.domain.task.dto.response;

import com.project.devlog.domain.task.entity.enums.TaskStatus;
import java.util.List;

public record TaskStatusListResponse(
        List<TaskStatus> taskStatusList
) {
}
