package com.project.devlog.domain.project.repository;

import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.project.entity.ProjectUser;
import com.project.devlog.domain.project.entity.enums.ProjectUserRole;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {

    @Query("select pu.role from ProjectUser pu "
            + "where pu.project.id = :projectId "
            + "and pu.user.id = :userId "
            + "and pu.isDeleted = false")
    Optional<ProjectUserRole> findRoleByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    List<ProjectUser> findByProjectIdAndIsDeletedFalse(Long projectId);
}
