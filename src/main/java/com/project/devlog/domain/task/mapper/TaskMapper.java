package com.project.devlog.domain.task.mapper;

import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.tag.entity.Tag;
import com.project.devlog.domain.task.dto.request.CreateTaskRequest;
import com.project.devlog.domain.task.dto.response.TaskIdResponse;
import com.project.devlog.domain.task.entity.Task;
import com.project.devlog.domain.task.entity.TaskTag;
import com.project.devlog.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public TaskIdResponse toIdDto(Long taskId) {
        return new TaskIdResponse(taskId);
    }

    public Task toTask(CreateTaskRequest request, Project project, User assignee) {
        return Task.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status())
                .priority(request.priority())
                .dueDate(request.dueDate())
                .assignee(assignee)
                .project(project)
                .build();
    }

    public TaskTag toTaskTag(Task task, Tag tag) {
        return TaskTag.builder()
                .task(task)
                .tag(tag)
                .build();
    }
}
