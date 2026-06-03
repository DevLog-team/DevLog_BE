package com.project.devlog.domain.task.controller;

import com.project.devlog.domain.task.dto.request.CreateTaskRequest;
import com.project.devlog.domain.task.dto.response.TaskIdResponse;
import com.project.devlog.domain.task.mapper.TaskMapper;
import com.project.devlog.domain.task.service.TaskService;
import com.project.devlog.global.annotation.CurrentUser;
import com.project.devlog.global.util.UrlCreator;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private static final String DEFAULT_URL = "/api/tasks";

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @PostMapping("/api/task")
    public ResponseEntity<TaskIdResponse> create( @Valid @RequestBody CreateTaskRequest request ) {
        Long taskId = taskService.create(request);
        URI location = UrlCreator.createUri(DEFAULT_URL, taskId);
        return ResponseEntity.created(location).body(taskMapper.toIdDto(taskId));
    }
}
