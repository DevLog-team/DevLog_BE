package com.project.devlog.domain.project.entity.projection;

import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;

public record ProjectListProjection(Long projectId,
                                    String title,
                                    String description,
                                    ProjectStatus status,
                                    LocalDate endDate,

                                    Long totalTaskCount,
                                    Long completedTaskCount,
                                    Long inProgressTaskCount,
                                    double progressRate
) {

    @QueryProjection
    public ProjectListProjection(Long projectId, String title, String description,
                               ProjectStatus status, LocalDate endDate,
                               long totalTaskCount, long completedTaskCount, long inProgressTaskCount) {
        this(
                projectId,
                title,
                description,
                status,
                endDate,
                totalTaskCount,
                completedTaskCount,
                inProgressTaskCount,
                calculateProgressRate(totalTaskCount, completedTaskCount)
        );
    }

    private static double calculateProgressRate(long totalTaskCount, long completedTaskCount) {
        if (totalTaskCount == 0)
            return 0.0;
        return Math.round((double) completedTaskCount / totalTaskCount * 100) / 100.0;
    }
}
