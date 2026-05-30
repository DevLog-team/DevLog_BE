package com.project.devlog.domain.project.mapper;

import com.project.devlog.domain.project.dto.request.CreateProjectRequest;
import com.project.devlog.domain.project.dto.response.ProjectDetailResponse;
import com.project.devlog.domain.project.entity.projection.ProjectProjection;
import com.project.devlog.global.response.dto.PageInfo;
import com.project.devlog.domain.project.dto.response.ProjectIdResponse;
import com.project.devlog.domain.project.dto.response.ProjectListResponse;
import com.project.devlog.domain.project.dto.response.ProjectSummaryResponse;
import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.project.entity.ProjectUser;
import com.project.devlog.domain.project.entity.enums.ProjectUserRole;
import com.project.devlog.domain.project.entity.projection.ProjectListProjection;
import com.project.devlog.domain.user.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
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

    public ProjectListResponse toProjectListResponse(Page<ProjectListProjection> projectList) {
        List<ProjectSummaryResponse> content = projectList.getContent().stream().map(project -> {
            return new ProjectSummaryResponse(
                    project.projectId(),
                    project.title(),
                    project.description(),
                    project.status(),
                    project.endDate(),
                    project.totalTaskCount(),
                    project.completedTaskCount(),
                    project.inProgressTaskCount(),
                    project.progressRate()
            );
        }).toList();

        PageInfo pageInfo = new PageInfo(
                projectList.getNumber() + 1,
                projectList.getSize(),
                projectList.getTotalElements(),
                projectList.getTotalPages(),
                projectList.isFirst(),
                projectList.isLast()
        );

        return new ProjectListResponse(content, pageInfo);
    }

    public ProjectDetailResponse ProjectDetailResponse(ProjectProjection project) {
        return new ProjectDetailResponse(
                project.projectId(),
                project.title(),
                project.description(),
                project.status(),
                project.startDate(),
                project.endDate(),
                project.memberCount(),
                project.totalTaskCount(),
                project.completedTaskCount(),
                project.inProgressTaskCount(),
                project.delayedTaskCount()
        );
    }
}
