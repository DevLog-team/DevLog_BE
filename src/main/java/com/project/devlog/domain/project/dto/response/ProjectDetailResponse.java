package com.project.devlog.domain.project.dto.response;

import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.project.devlog.domain.project.entity.enums.ProjectUserRole;
import java.time.LocalDate;

public record ProjectDetailResponse(
        Long projectId,
        String title,
        String description,
        ProjectStatus status,
        LocalDate startDate,
        LocalDate endDate,
        ProjectUserRole role,
        Long memberCount,
        Long totalTaskCount,
        Long completedTaskCount,
        Long inProgressTaskCount,
        Long delayedTaskCount
) { }
