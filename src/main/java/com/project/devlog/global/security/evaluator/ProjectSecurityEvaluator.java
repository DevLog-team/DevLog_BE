package com.project.devlog.global.security.evaluator;

import com.project.devlog.domain.project.entity.enums.ProjectUserRole;
import com.project.devlog.domain.project.repository.ProjectUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("projectSecurity")
@RequiredArgsConstructor
public class ProjectSecurityEvaluator {

    private final ProjectUserRepository projectUserRepository;

    public boolean isOwner(Long projectId, Long userId) {
        if (projectId == null || userId == null) { return false; }

        return projectUserRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> role == ProjectUserRole.OWNER)
                .orElse(false);
    }

}
