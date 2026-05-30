package com.project.devlog.domain.project.entity;

import com.project.devlog.domain.project.entity.enums.ProjectUserRole;
import com.project.devlog.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "project_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ProjectUserRole role;

    private boolean isDeleted = false;

    @Builder
    private ProjectUser(Project project, User user, ProjectUserRole role) {
        addProject(project);
        addUser(user);
        this.role = role;
    }

    private void addProject(Project project) {
        if (project != null) {
            this.project = project;
            project.getProjectUsers().add(this);
        }
    }

    private void addUser(User user) {
        if (user != null) {
            this.user = user;
            user.getProjectUsers().add(this);
        }
    }

    public void delete() {
        isDeleted = true;
    }
}
