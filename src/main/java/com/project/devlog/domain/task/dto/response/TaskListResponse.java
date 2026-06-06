package com.project.devlog.domain.task.dto.response;

import com.project.devlog.global.response.dto.PageInfo;
import java.util.List;

public record TaskListResponse(
        List<TaskSummaryResponse> content,
        PageInfo pageInfo
) {
}
