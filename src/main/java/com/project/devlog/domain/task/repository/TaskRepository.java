package com.project.devlog.domain.task.repository;

import com.project.devlog.domain.task.dto.request.TaskSearchCondition;
import com.project.devlog.domain.task.entity.Task;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom {
    @Query("select distinct t from Task t "
            + "left join fetch t.assignee "
            + "left join fetch t.tags tt "
            + "left join fetch tt.tag "
            + "where t.project.id = :projectId and t.isDeleted = false")
    List<Task> findAllByProjectIdAndIsDeletedFalse(@Param("projectId") Long projectId);

    @Query("select t from Task t "
            + "left join fetch t.assignee "
            + "left join fetch t.tags tt "
            + "left join fetch tt.tag "
            + "where t.id = :taskId and t.isDeleted = false")
    Optional<Task> findByIdFetchUserAndTag(@Param("taskId") Long taskId);

    @Query("select t.project.id from Task t "
            + "where t.id = :taskId and t.isDeleted = false")
    Long findProjectIdByTaskId(Long taskId);

    Optional<Task> findByIdAndIsDeletedFalse(Long taskId);
}
