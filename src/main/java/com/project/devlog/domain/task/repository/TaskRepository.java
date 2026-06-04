package com.project.devlog.domain.task.repository;

import com.project.devlog.domain.task.entity.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("select distinct t from Task t "
            + "left join fetch t.assignee "
            + "left join fetch t.tags tt "
            + "left join fetch tt.tag "
            + "where t.project.id = :projectId and t.isDeleted = false")
    List<Task> findAllByProjectIdAndIsDeletedFalse(@Param("projectId") Long projectId);
}
