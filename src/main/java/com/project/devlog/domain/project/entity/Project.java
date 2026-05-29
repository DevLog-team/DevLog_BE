package com.project.devlog.domain.project.entity;

import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.project.devlog.global.audting.BaseDateTime;
import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.errorcode.ProjectErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "project")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @OneToMany(mappedBy = "project")
    private List<ProjectUser> projectUsers = new ArrayList<>();

    private boolean isDeleted = false;

    @Builder
    private Project(Long id, String title, String description, ProjectStatus status, LocalDate startDate,
                    LocalDate endDate) {
        validateDates(startDate, endDate);

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BusinessException(ProjectErrorCode.PROJECT_DATE_REQUIRED);
        }

        if (endDate.isBefore(startDate)) {
            throw new BusinessException(ProjectErrorCode.INVALID_PROJECT_DATE);
        }
    }

    public void update(String title, String description, ProjectStatus status, LocalDate startDate, LocalDate endDate) {
        validateDates(startDate, endDate);

        this.title = title;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
