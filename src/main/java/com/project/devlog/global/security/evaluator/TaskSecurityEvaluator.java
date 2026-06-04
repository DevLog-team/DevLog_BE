package com.project.devlog.global.security.evaluator;

import com.project.devlog.domain.project.repository.ProjectUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("taskSecurity")
@RequiredArgsConstructor
public class TaskSecurityEvaluator {

    private final ProjectUserRepository projectUserRepository;

    public boolean isMember(Long projectId, Long userId) {
        if (projectId == null || userId == null) { return false; }

        return projectUserRepository.existsByProjectIdAndUserIdAndIsDeletedFalse(projectId, userId);
    }
}
