package com.project.devlog.domain.project.controller;

import com.project.devlog.domain.project.dto.request.CreateProjectRequest;
import com.project.devlog.domain.project.dto.request.ProjectSearchCondition;
import com.project.devlog.domain.project.dto.response.ProjectIdResponse;
import com.project.devlog.domain.project.dto.response.ProjectListResponse;
import com.project.devlog.domain.project.entity.projection.ProjectListProjection;
import com.project.devlog.domain.project.mapper.ProjectMapper;
import com.project.devlog.domain.project.service.ProjectService;
import com.project.devlog.global.annotation.CurrentUser;
import com.project.devlog.global.util.UrlCreator;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @GetMapping("/api/projects")
    public ResponseEntity<ProjectListResponse> getList(
            @CurrentUser Long userId,
            @ModelAttribute ProjectSearchCondition condition,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable)
    {
        Page<ProjectListProjection> projectList = projectService.getList(userId, condition, pageable);
        return ResponseEntity.ok().body(projectMapper.toProjectList(projectList));
    }

}
