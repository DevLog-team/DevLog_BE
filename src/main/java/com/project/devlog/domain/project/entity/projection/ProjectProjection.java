package com.project.devlog.domain.project.entity.projection;

import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;

public record ProjectProjection(
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
) {

    @QueryProjection
    public ProjectProjection {}
}