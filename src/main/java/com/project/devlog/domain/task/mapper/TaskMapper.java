package com.project.devlog.domain.task.mapper;

import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.tag.entity.Tag;
import com.project.devlog.domain.task.dto.request.CreateTaskRequest;
import com.project.devlog.domain.task.dto.response.TagResponse;
import com.project.devlog.domain.task.dto.response.TaskIdResponse;
import com.project.devlog.domain.task.dto.response.TaskListResponse;
import com.project.devlog.domain.task.dto.response.TaskPriorityListResponse;
import com.project.devlog.domain.task.dto.response.TaskResponse;
import com.project.devlog.domain.task.dto.response.TaskStatusListResponse;
import com.project.devlog.domain.task.dto.response.TaskSummaryResponse;
import com.project.devlog.domain.task.entity.Task;
import com.project.devlog.domain.task.entity.TaskTag;
import com.project.devlog.domain.task.entity.enums.TaskPriority;
import com.project.devlog.domain.task.entity.enums.TaskStatus;
import com.project.devlog.domain.user.entity.User;
import com.project.devlog.global.response.dto.PageInfo;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
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

    public TaskResponse toKanbanTaskResponse(Task task) {
        return TaskResponse.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .assigneeName(
                        task.getAssignee() != null ? task.getAssignee().getName() : null)
                .tags(getTags(task))
                .build();
    }

    private List<TagResponse> getTags(Task task) {
        return task.getTags().stream()
                .filter(taskTag -> !taskTag.isDeleted())
                .map(taskTag -> toTagResponse(taskTag.getTag()))
                .collect(Collectors.toList());
    }

    public TagResponse toTagResponse(Tag tag) {
        return TagResponse.builder()
                .tagId(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .build();
    }


    public TaskListResponse toTaskListResponse(Page<Task> taskList) {
        List<TaskSummaryResponse> content = taskList.getContent().stream().map(task -> {
            return TaskSummaryResponse.builder()
                    .taskId(task.getId())
                    .title(task.getTitle())
                    .status(task.getStatus())
                    .priority(task.getPriority())
                    .dueDate(task.getDueDate())
                    .assigneeName(task.getAssignee().getName())
                    .build();
        }).toList();

        PageInfo pageInfo = new PageInfo(
                taskList.getNumber() + 1,
                taskList.getSize(),
                taskList.getTotalElements(),
                taskList.getTotalPages(),
                taskList.isFirst(),
                taskList.isLast()
        );

        return new TaskListResponse(content, pageInfo);
    }

    public TaskResponse toTaskResponse(Task task) {
        return TaskResponse.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .assigneeName(task.getAssignee().getName())
                .tags(getTags(task))
                .build();
    }
}
