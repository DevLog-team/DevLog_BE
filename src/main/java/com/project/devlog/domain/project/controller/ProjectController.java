package com.project.devlog.domain.project.controller;

import com.project.devlog.domain.project.mapper.ProjectMapper;
import com.project.devlog.domain.project.dto.request.CreateProjectRequest;
import com.project.devlog.domain.project.dto.response.ProjectIdResponse;
import com.project.devlog.domain.project.service.ProjectService;
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
public class ProjectController {

    private static final String DEFAULT_URL = "/api/projects";

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    @PostMapping("/api/project")
    public ResponseEntity<ProjectIdResponse> create(
            @Valid @RequestBody CreateProjectRequest request,
            @CurrentUser Long userId)
    {
        Long projectId = projectService.create(userId, request);
        URI location = UrlCreator.createUri(DEFAULT_URL, projectId);
        return ResponseEntity.created(location).body(projectMapper.toIdDTo(projectId));
    }
}
