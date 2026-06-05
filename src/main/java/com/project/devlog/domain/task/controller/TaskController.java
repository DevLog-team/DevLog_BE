package com.project.devlog.domain.task.controller;

import com.project.devlog.domain.task.dto.request.ChangePriorityRequest;
import com.project.devlog.domain.task.dto.request.ChangeStatusRequest;
import com.project.devlog.domain.task.dto.request.CreateTaskRequest;
import com.project.devlog.domain.task.dto.request.TaskSearchCondition;
import com.project.devlog.domain.task.dto.request.UpdateTaskRequest;
import com.project.devlog.domain.task.dto.response.KanbanBoardResponse;
import com.project.devlog.domain.task.dto.response.TaskIdResponse;
import com.project.devlog.domain.task.dto.response.TaskListResponse;
import com.project.devlog.domain.task.dto.response.TaskPriorityListResponse;
import com.project.devlog.domain.task.dto.response.TaskResponse;
import com.project.devlog.domain.task.dto.response.TaskStatusListResponse;
import com.project.devlog.domain.task.entity.Task;
import com.project.devlog.domain.task.entity.enums.TaskPriority;
import com.project.devlog.domain.task.entity.enums.TaskStatus;
import com.project.devlog.domain.task.mapper.TaskMapper;
import com.project.devlog.domain.task.service.TaskService;
import com.project.devlog.global.annotation.CurrentUser;
import com.project.devlog.global.util.UrlCreator;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private static final String DEFAULT_URL = "/api/tasks";

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @PostMapping("/api/projects/{projectId}/tasks")
    @PreAuthorize("@taskSecurity.isProjectMember(#projectId, #userId)")
    public ResponseEntity<TaskIdResponse> create(
            @CurrentUser Long userId,
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequest request) {
        Long taskId = taskService.create(projectId, request);
        URI location = UrlCreator.createUri(DEFAULT_URL, taskId);
        return ResponseEntity.created(location).body(taskMapper.toIdDto(taskId));
    }

    @GetMapping("/api/tasks/status")
    public ResponseEntity<TaskStatusListResponse> getStatusList() {
        List<TaskStatus> statusList = taskService.getStatusList();
        return ResponseEntity.ok().body(taskMapper.toTaskStatusResponse(statusList));
    }

    @GetMapping("/api/tasks/priority")
    public ResponseEntity<TaskPriorityListResponse> getPriorityList() {
        List<TaskPriority> priorities = taskService.getPriorityList();
        return ResponseEntity.ok().body(taskMapper.toTaskPriorityListResponse(priorities));
    }

    @GetMapping("/api/projects/{projectId}/tasks/kanban")
    @PreAuthorize("@taskSecurity.isProjectMember(#projectId, #userId)")
    public ResponseEntity<KanbanBoardResponse> getTasksForKanban(
            @CurrentUser Long userId,
            @PathVariable Long projectId
    ) {
        KanbanBoardResponse responseData = taskService.getKanbanBoard(projectId);
        return ResponseEntity.ok().body(responseData);
    }

    @GetMapping("/api/projects/{projectId}/tasks")
    @PreAuthorize("@taskSecurity.isProjectMember(#projectId, #userId)")
    public ResponseEntity<TaskListResponse> getList(
            @CurrentUser Long userId,
            @PathVariable Long projectId,
            @ModelAttribute TaskSearchCondition condition,
            @PageableDefault(size = 10, sort = "dueDate", direction = Direction.ASC) Pageable pageable
    ) {
        Page<Task> taskList = taskService.getList(projectId, condition, pageable);
        return ResponseEntity.ok().body(taskMapper.toTaskListResponse(taskList));
    }

    @GetMapping("/api/tasks/{taskId}")
    @PreAuthorize("@taskSecurity.isTaskAccessAllowed(#taskId, #userId)")
    public ResponseEntity<TaskResponse> getDetails(
            @CurrentUser Long userId,
            @PathVariable Long taskId
    ) {
        Task task = taskService.getDetails(taskId);
        return ResponseEntity.ok().body(taskMapper.toTaskResponse(task));
    }

    @PatchMapping("/api/tasks/{taskId}")
    @PreAuthorize("@taskSecurity.isTaskAccessAllowed(#taskId, #userId)")
    public ResponseEntity<TaskIdResponse> updateBasicInfo(
            @CurrentUser Long userId,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        taskService.updateBasicInfo(taskId, request);
        return ResponseEntity.ok().body(taskMapper.toIdDto(taskId));
    }

    @PatchMapping("/api/tasks/{taskId}/status")
    @PreAuthorize("@taskSecurity.isTaskAccessAllowed(#taskId, #userId)")
    public ResponseEntity<TaskIdResponse> changeStatus(
            @CurrentUser Long userId,
            @PathVariable Long taskId,
            @Valid @RequestBody ChangeStatusRequest request
    ) {
        taskService.changeStatus(taskId, request);
        return ResponseEntity.ok().body(taskMapper.toIdDto(taskId));
    }

    @PatchMapping("/api/tasks/{taskId}/priority")
    @PreAuthorize("@taskSecurity.isTaskAccessAllowed(#taskId, #userId)")
    public ResponseEntity<TaskIdResponse> changePriority(
            @CurrentUser Long userId,
            @PathVariable Long taskId,
            @Valid @RequestBody ChangePriorityRequest request
    ) {
        taskService.changePriority(taskId, request);
        return ResponseEntity.ok().body(taskMapper.toIdDto(taskId));
    }
}
