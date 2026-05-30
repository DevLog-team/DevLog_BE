package com.project.devlog.domain.project.mock;

import com.project.devlog.domain.project.dto.request.CreateProjectRequest;
import com.project.devlog.domain.project.dto.request.UpdateProjectRequest;
import com.project.devlog.domain.project.dto.response.ProjectListResponse;
import com.project.devlog.domain.project.dto.response.ProjectSummaryResponse;
import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.project.devlog.domain.project.entity.enums.ProjectUserRole;
import com.project.devlog.domain.project.entity.projection.ProjectListProjection;
import com.project.devlog.domain.project.entity.projection.ProjectProjection;
import com.project.devlog.global.response.dto.PageInfo;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class ProjectMock {

    private final Long projectId = 1L;
    private final String title = "스프링부트 프로젝트";
    private final String description = "개발 프로젝트 설명입니다.";
    private final ProjectStatus status = ProjectStatus.ACTIVE;
    private final LocalDate startDate = LocalDate.now();
    private final LocalDate endDate = LocalDate.now().plusMonths(3);

    private final long totalTaskCount = 10L;
    private final long completedTaskCount = 6L;
    private final long inProgressTaskCount = 4L;
    private final long delayedTaskCount = 2L;
    private final double progressRate = 60.0;

    public Project domainMock(ProjectStatus status) {
        return Project.builder()
                .id(projectId)
                .title(title)
                .description(description)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public CreateProjectRequest createRequestMock() {
        return new CreateProjectRequest(
                title,
                description,
                ProjectStatus.ACTIVE,
                startDate,
                endDate
        );
    }

    public ProjectListProjection listProjectionMock() {
        return new ProjectListProjection(
                projectId,
                title,
                description,
                status,
                endDate,
                totalTaskCount,
                completedTaskCount,
                inProgressTaskCount
        );
    }

    public Page<ProjectListProjection> pageProjectionMock(Pageable pageable) {
        List<ProjectListProjection> content = List.of(listProjectionMock());
        return new PageImpl<>(content, pageable, 1);
    }

    public ProjectListResponse listResponseMock() {
        ProjectSummaryResponse summary = new ProjectSummaryResponse(
                projectId,
                title,
                description,
                status,
                endDate,
                totalTaskCount,
                completedTaskCount,
                inProgressTaskCount,
                progressRate
        );

        PageInfo pageInfo = new PageInfo(1, 10, 1, 1, true, true);
        return new ProjectListResponse(List.of(summary), pageInfo);
    }

    public ProjectProjection projectProjectionMock() {
        return new ProjectProjection(
                projectId,
                title,
                description,
                status,
                startDate,
                endDate,
                2L,
                totalTaskCount,
                completedTaskCount,
                inProgressTaskCount,
                delayedTaskCount
        );
    }

    public UpdateProjectRequest updateRequestMock() {
        return new UpdateProjectRequest(
                "수정된 프로젝트 제목",
                "수정된 상세 설명입니다.",
                ProjectStatus.COMPLETED,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusMonths(1)
        );
    }
}
