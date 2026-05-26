package com.project.devlog.domain.project.mock;

import com.project.devlog.domain.project.dto.request.CreateProjectRequest;
import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.project.devlog.domain.project.entity.enums.ProjectUserRole;
import com.project.devlog.domain.user.entity.User;
import com.project.devlog.global.security.dto.request.LoginRequest;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class ProjectMock {

    private final String title = "테스트";
    private final String description = "테스트 입니다.";
    private final LocalDate startDate = LocalDate.now();
    private final LocalDate endDate = LocalDate.now().plusDays(1);

    public Project domainMock(ProjectStatus status) {
        return Project.builder()
                .id(1L)
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
                ProjectUserRole.OWNER,
                startDate,
                endDate
        );
    }
}
