package com.project.devlog.global.security.evaluator;

import com.project.devlog.domain.project.repository.ProjectUserRepository;
import com.project.devlog.domain.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("taskSecurity")
@RequiredArgsConstructor
public class TaskSecurityEvaluator {

    private final TaskRepository taskRepository;
    private final ProjectUserRepository projectUserRepository;

    public boolean isProjectMember(Long projectId, Long userId) {
        if (projectId == null || userId == null) { return false; }

        return projectUserRepository.existsByProjectIdAndUserIdAndIsDeletedFalse(projectId, userId);
    }

    public boolean isTaskAccessAllowed(Long taskId, Long userId) {
        if (taskId == null || userId == null) { return false; }

        Long projectId = taskRepository.findProjectIdByTaskId(taskId);

        if (projectId == null) { return false; }

        return projectUserRepository.existsByProjectIdAndUserIdAndIsDeletedFalse(projectId, userId);
    }
}
