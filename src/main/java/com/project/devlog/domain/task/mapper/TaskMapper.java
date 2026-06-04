package com.project.devlog.domain.task.mapper;

import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.tag.entity.Tag;
import com.project.devlog.domain.task.dto.request.CreateTaskRequest;
import com.project.devlog.domain.task.dto.response.KanbanTaskResponse;
import com.project.devlog.domain.task.dto.response.TagResponse;
import com.project.devlog.domain.task.dto.response.TaskIdResponse;
import com.project.devlog.domain.task.dto.response.TaskPriorityListResponse;
import com.project.devlog.domain.task.dto.response.TaskStatusListResponse;
import com.project.devlog.domain.task.entity.Task;
import com.project.devlog.domain.task.entity.TaskTag;
import com.project.devlog.domain.task.entity.enums.TaskPriority;
import com.project.devlog.domain.task.entity.enums.TaskStatus;
import com.project.devlog.domain.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
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

    public TaskStatusListResponse toTaskStatusResponse(List<TaskStatus> statusList) {
        return new TaskStatusListResponse(statusList);
    }

    public TaskPriorityListResponse toTaskPriorityListResponse(List<TaskPriority> priorities) {
        return new TaskPriorityListResponse(priorities);
    }

    public KanbanTaskResponse toKanbanTaskResponse(Task task) {
        return KanbanTaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .assigneeName(
                        task.getAssignee() != null ? task.getAssignee().getName() : null)
                .tags(task.getTags().stream()
                        .filter(taskTag -> !taskTag.isDeleted())
                        .map(taskTag -> toTagResponse(taskTag.getTag()))
                        .collect(Collectors.toList()))
                .build();
    }

    public TagResponse toTagResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .build();
    }


}
