package com.project.devlog.domain.project.repository;

import com.project.devlog.domain.project.entity.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
}
