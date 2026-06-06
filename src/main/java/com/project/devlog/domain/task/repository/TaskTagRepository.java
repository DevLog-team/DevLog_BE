package com.project.devlog.domain.task.repository;

import com.project.devlog.domain.task.entity.TaskTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskTagRepository extends JpaRepository<TaskTag, Long> {
}
