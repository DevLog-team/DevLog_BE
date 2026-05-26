package com.project.devlog.domain.project.mapper;

import com.project.devlog.domain.project.dto.request.CreateProjectRequest;
import com.project.devlog.domain.project.dto.response.ProjectIdResponse;
import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.project.entity.ProjectUser;
import com.project.devlog.domain.project.entity.enums.ProjectUserRole;
import com.project.devlog.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {
    public ProjectIdResponse toIdDTo(Long projectId) {
        return new ProjectIdResponse(projectId);
    }

    public Project toProject(CreateProjectRequest request) {
        return Project.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();
    }

    public ProjectUser toProjectUser(Project project, User user, ProjectUserRole role) {
        return ProjectUser.builder()
                .project(project)
                .user(user)
                .role(role)
                .build();
    }
}
