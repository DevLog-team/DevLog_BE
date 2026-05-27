package com.project.devlog.domain.project.repository;

import com.project.devlog.domain.project.dto.request.ProjectSearchCondition;
import com.project.devlog.domain.project.entity.projection.ProjectListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepositoryCustom {
    Page<ProjectListProjection> searchUserProjects(Long userId, ProjectSearchCondition condition, Pageable pageable);
}
