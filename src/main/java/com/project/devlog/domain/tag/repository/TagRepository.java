package com.project.devlog.domain.tag.repository;

import com.project.devlog.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsTagByNameAndIsDeletedFalse(String name);
}
