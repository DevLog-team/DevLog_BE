package com.project.devlog.domain.task.entity;

import com.project.devlog.domain.tag.entity.Tag;
import com.project.devlog.global.audting.BaseDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "task_tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskTag extends BaseDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    private boolean isDeleted = false;

    @Builder
    private TaskTag(Task task, Tag tag) {
        addTask(task);
        this.tag = tag;
    }

    private void addTask(Task task) {
        if (task != null) {
            this.task = task;
            task.getTags().add(this);
        }
    }
}
