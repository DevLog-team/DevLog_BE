package com.project.devlog.domain.task.repository;

import com.project.devlog.domain.task.dto.request.TaskSearchCondition;
import com.project.devlog.domain.task.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskRepositoryCustom {
    Page<Task> searchUserProjects(Long projectId, TaskSearchCondition condition, Pageable pageable);
}
