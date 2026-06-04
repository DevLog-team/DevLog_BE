package com.project.devlog.domain.task.dto.response;

import com.project.devlog.domain.task.entity.enums.TaskStatus;
import java.util.List;
import java.util.Map;

public record KanbanBoardResponse(
        Map<TaskStatus, List<TaskResponse>> board
) { }
