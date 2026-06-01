package com.project.devlog.domain.project.dto.response;

import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.project.devlog.domain.project.entity.enums.ProjectUserRole;
import java.time.LocalDate;

public record ProjectSummaryResponse(
        Long projectId,
        String title,
        String description,
        ProjectStatus status,
        LocalDate endDate,
        ProjectUserRole role,
        long totalTaskCount,
        long completedTaskCount,
        long inProgressTaskCount,
        double progressRate
) {

}
