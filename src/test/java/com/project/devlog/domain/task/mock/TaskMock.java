package com.project.devlog.domain.task.mock;

import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.task.dto.request.CreateTaskRequest;
import com.project.devlog.domain.task.dto.response.KanbanBoardResponse;
import com.project.devlog.domain.task.dto.response.TagResponse;
import com.project.devlog.domain.task.dto.response.TaskListResponse;
import com.project.devlog.domain.task.dto.response.TaskResponse;
import com.project.devlog.domain.task.entity.Task;
import com.project.devlog.domain.task.entity.enums.TaskPriority;
import com.project.devlog.domain.task.entity.enums.TaskStatus;
import com.project.devlog.domain.user.entity.User;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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

    public Task taskDomainMock(User assignee, Project project, TaskStatus status, TaskPriority priority) {
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

    public TaskResponse kanbanTaskResponseMock(TaskStatus status) {
        TagResponse tagMock = new TagResponse(
                10L,
                "Frontend",
                "#3357FF"
        );

        return TaskResponse.builder()
                .id(taskId)
                .title(title)
                .description(description)
                .status(status)
                .priority(priority)
                .dueDate(dueDate)
                .assigneeName("데브로그팀원")
                .tags(List.of(tagMock))
                .build();
    }

    public KanbanBoardResponse kanbanBoardResponseMock() {
        Map<TaskStatus, List<TaskResponse>> mockMap = new EnumMap<>(TaskStatus.class);

        for (TaskStatus status : TaskStatus.values()) {
            mockMap.put(status, new java.util.ArrayList<>());
        }

        mockMap.get(TaskStatus.TODO).add(kanbanTaskResponseMock(TaskStatus.TODO));

        return new KanbanBoardResponse(mockMap);
    }

    public Page<Task> taskPageMock(Pageable pageable) {

        User mockAssignee = User.builder()
                .id(1L)
                .name("개발자A")
                .build();

        Project mockProject = Project.builder()
                .id(1L)
                .title("테스트 프로젝트")
                .startDate(java.time.LocalDate.now())
                .endDate(java.time.LocalDate.now().plusMonths(1))
                .build();

        List<Task> content = List.of(taskDomainMock(mockAssignee, mockProject, TaskStatus.TODO, TaskPriority.HIGH));
        return new PageImpl<>(content, pageable, content.size());
    }

}
