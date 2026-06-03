package com.project.devlog.domain.task.dto.request;

import com.project.devlog.domain.task.entity.enums.TaskPriority;
import com.project.devlog.domain.task.entity.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.validator.constraints.Length;

public record CreateTaskRequest(
        @NotNull Long projectId,
        @NotBlank String title,
        @Length(max = 500, message = "작업 설명은 500자 이내로 작성해주세요.") String description,
        @NotNull TaskStatus status,
        @NotNull TaskPriority priority,
        @NotNull LocalDate dueDate,
        List<Long> tagIds,
        @NotNull Long assigneeId
) {
}
