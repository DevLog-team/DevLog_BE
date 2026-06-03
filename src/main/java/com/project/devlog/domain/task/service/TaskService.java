package com.project.devlog.domain.task.service;

import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.project.repository.ProjectRepository;
import com.project.devlog.domain.tag.entity.Tag;
import com.project.devlog.domain.tag.repository.TagRepository;
import com.project.devlog.domain.task.dto.request.CreateTaskRequest;
import com.project.devlog.domain.task.entity.Task;
import com.project.devlog.domain.task.entity.TaskTag;
import com.project.devlog.domain.task.entity.enums.TaskPriority;
import com.project.devlog.domain.task.entity.enums.TaskStatus;
import com.project.devlog.domain.task.mapper.TaskMapper;
import com.project.devlog.domain.task.repository.TaskRepository;
import com.project.devlog.domain.task.repository.TaskTagRepository;
import com.project.devlog.domain.user.entity.User;
import com.project.devlog.domain.user.repository.UserRepository;
import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.errorcode.ProjectErrorCode;
import com.project.devlog.global.exception.errorcode.TagErrorCode;
import com.project.devlog.global.exception.errorcode.UserErrorCode;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskMapper taskMapper;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskTagRepository taskTagRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public Long create(CreateTaskRequest request) {
        User assignee = findUserById(request.assigneeId());
        Project project = findProjectById(request.projectId());

        Task task = taskMapper.toTask(request, project, assignee);
        taskRepository.save(task);

        if (hasTags(request)) {
            List<TaskTag> taskTags = request.tagIds().stream()
                    .map(tagId -> {
                        Tag tag = findTagById(tagId);
                        return taskMapper.toTaskTag(task, tag);
                    })
                    .toList();
            taskTagRepository.saveAll(taskTags);
        }

        return task.getId();
    }

    private User findUserById(Long userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_EXIST_USER));
    }

    private Project findProjectById(Long projectId) {
        return projectRepository.findProjectByIdAndIsDeletedFalse(projectId)
                .orElseThrow(() -> new BusinessException(ProjectErrorCode.PROJECT_NOT_FOUND));
    }

    private Tag findTagById(Long tagId) {
        return tagRepository.findByIdAndIsDeletedFalse(tagId)
                .orElseThrow(() -> new BusinessException(TagErrorCode.TAG_NOT_FOUND));
    }

    private boolean hasTags(CreateTaskRequest request) {
        return request.tagIds() != null && !request.tagIds().isEmpty();
    }

    public List<TaskStatus> getStatusList() {
        return Arrays.asList(TaskStatus.values());
    }

    public List<TaskPriority> getPriorityList() {
        return Arrays.asList(TaskPriority.values());
    }
}
