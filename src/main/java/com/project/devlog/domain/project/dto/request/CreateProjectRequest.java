package com.project.devlog.domain.project.dto.request;

import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.project.devlog.domain.project.entity.enums.ProjectUserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.hibernate.validator.constraints.Length;

public record CreateProjectRequest(
        @NotBlank String title,
        @Length(max = 500, message = "프로젝트 설명은 500자 이내로 작성해주세요.") String description,
        @NotNull ProjectStatus status,
        @NotNull ProjectUserRole role,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {
}
