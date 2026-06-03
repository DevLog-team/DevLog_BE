package com.project.devlog.domain.tag.repository;

import com.project.devlog.domain.tag.entity.Tag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsTagByNameAndIsDeletedFalse(String name);

    Optional<Tag> findByIdAndIsDeletedFalse(Long tagId);
}
