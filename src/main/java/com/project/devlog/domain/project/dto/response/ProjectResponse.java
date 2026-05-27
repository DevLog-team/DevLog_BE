package com.project.devlog.domain.project.dto.response;

import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import java.time.LocalDate;

public record ProjectResponse(
        Long projectId,
        String title,
        String description,
        ProjectStatus status,
        LocalDate startDate,
        LocalDate endDate,
        Long memberCount,
        Long totalTaskCount,
        Long completedTaskCount,
        Long inProgressTaskCount,
        Long delayedTaskCount
) { }
