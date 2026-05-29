package com.project.devlog.domain.project.dto.request;

import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.hibernate.validator.constraints.Length;

public record UpdateProjectRequest(
        @NotNull String title,
        @Length(max = 500, message = "프로젝트 설명은 500자 이내로 작성해주세요.") String description,
        @NotNull ProjectStatus status,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {
}
