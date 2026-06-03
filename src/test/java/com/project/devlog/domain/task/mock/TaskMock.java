package com.project.devlog.domain.task.mock;

import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.task.dto.request.CreateTaskRequest;
import com.project.devlog.domain.task.entity.Task;
import com.project.devlog.domain.task.entity.enums.TaskPriority;
import com.project.devlog.domain.task.entity.enums.TaskStatus;
import com.project.devlog.domain.user.entity.User;
import java.time.LocalDate;
import java.util.List;

public class TaskMock {

    private final Long taskId = 1L;
    private final Long projectId = 1L;
    private final Long assigneeId = 1L;
    private final String title = "인증 기능 구현";
    private final String description = "스프링 시큐리티 + JWT 기반 로그인 API 개발";
    private final TaskStatus status = TaskStatus.TODO;
    private final TaskPriority priority = TaskPriority.HIGH;
    private final LocalDate dueDate = LocalDate.now().plusDays(7);
    private final List<Long> tagIds = List.of(1L);

    public CreateTaskRequest createTaskRequestMock() {
        return new CreateTaskRequest(
                projectId,
                title,
                description,
                status,
                priority,
                dueDate,
                tagIds,
                assigneeId
        );
    }

    public CreateTaskRequest createTaskRequestWithoutTagsMock() {
        return new CreateTaskRequest(
                projectId,
                title,
                description,
                status,
                priority,
                dueDate,
                null, // 태그가 없는 케이스용
                5L
        );
    }

    public Task taskDomainMock(User assignee, Project project) {
        Task task = Task.builder()
                .id(taskId)
                .title(title)
                .description(description)
                .status(status)
                .priority(priority)
                .dueDate(dueDate)
                .assignee(assignee)
                .project(project)
                .build();

        return task;
    }
}
