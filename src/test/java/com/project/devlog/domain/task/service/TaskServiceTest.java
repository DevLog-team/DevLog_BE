package com.project.devlog.domain.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.project.devlog.domain.project.mock.ProjectMock;
import com.project.devlog.domain.project.repository.ProjectRepository;
import com.project.devlog.domain.tag.entity.Tag;
import com.project.devlog.domain.tag.mock.TagMock;
import com.project.devlog.domain.tag.repository.TagRepository;
import com.project.devlog.domain.task.dto.request.CreateTaskRequest;
import com.project.devlog.domain.task.entity.Task;
import com.project.devlog.domain.task.entity.TaskTag;
import com.project.devlog.domain.task.mapper.TaskMapper;
import com.project.devlog.domain.task.mock.TaskMock;
import com.project.devlog.domain.task.repository.TaskRepository;
import com.project.devlog.domain.task.repository.TaskTagRepository;
import com.project.devlog.domain.user.entity.User;
import com.project.devlog.domain.user.mock.UserMock;
import com.project.devlog.domain.user.repository.UserRepository;
import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.errorcode.UserErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService sut;

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskTagRepository taskTagRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskMapper taskMapper;

    @Mock
    PasswordEncoder encoder;

    private TaskMock taskMock;
    private UserMock userMock;
    private ProjectMock projectMock;
    private TagMock tagMock;

    @BeforeEach
    void setUp() {
        taskMock = new TaskMock();
        userMock = new UserMock(encoder);
        projectMock = new ProjectMock();
        tagMock = new TagMock();
    }

    @Nested
    @DisplayName("작업 생성 (create)")
    class CreateTask {

        @Test
        @DisplayName("성공: 태그 목록이 포함된 작업을 정상적으로 생성한다.")
        void success_withTags() {
            // given
            CreateTaskRequest request = taskMock.createTaskRequestMock();
            User assignee = userMock.domainMock();
            Project project = projectMock.domainMock(ProjectStatus.ACTIVE);
            Task task = taskMock.taskDomainMock(assignee, project);
            Tag tag = tagMock.domainMock();

            given(userRepository.findByIdAndIsDeletedFalse(request.assigneeId())).willReturn(Optional.of(assignee));
            given(projectRepository.findProjectByIdAndIsDeletedFalse(request.projectId())).willReturn(Optional.of(project));
            given(taskMapper.toTask(request, project, assignee)).willReturn(task);
            given(taskRepository.save(task)).willReturn(task);

            given(tagRepository.findByIdAndIsDeletedFalse(any())).willReturn(Optional.of(tag));
            given(taskMapper.toTaskTag(any(Task.class), any(Tag.class))).willReturn(mock(TaskTag.class));

            // when
            Long createdTaskId = sut.create(request);

            // then
            assertThat(createdTaskId).isEqualTo(task.getId());
            verify(taskRepository, times(1)).save(task);
            verify(taskTagRepository, times(1)).saveAll(anyList());
        }

        @Test
        @DisplayName("성공: 태그 정보가 없는 작업도 정상적으로 생성되며 매핑 엔티티는 저장하지 않는다.")
        void success_withoutTags() {
            // given
            CreateTaskRequest request = taskMock.createTaskRequestWithoutTagsMock();
            User assignee = userMock.domainMock();
            Project project = projectMock.domainMock(ProjectStatus.ACTIVE);
            Task task = taskMock.taskDomainMock(assignee, project);

            given(userRepository.findByIdAndIsDeletedFalse(request.assigneeId())).willReturn(Optional.of(assignee));
            given(projectRepository.findProjectByIdAndIsDeletedFalse(request.projectId())).willReturn(Optional.of(project));
            given(taskMapper.toTask(request, project, assignee)).willReturn(task);

            // when
            Long createdTaskId = sut.create(request);

            // then
            assertThat(createdTaskId).isEqualTo(task.getId());
            verify(taskRepository, times(1)).save(task);
            verify(taskTagRepository, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("실패: 존해하지 않거나 삭제된 담당자 ID인 경우 BusinessException이 발생한다.")
        void fail_userNotFound() {
            // given
            CreateTaskRequest request = taskMock.createTaskRequestMock();

            given(userRepository.findByIdAndIsDeletedFalse(request.assigneeId())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.create(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(UserErrorCode.NOT_EXIST_USER.getMessage());

            verify(projectRepository, never()).findProjectByIdAndIsDeletedFalse(anyLong());
            verify(taskRepository, never()).save(any(Task.class));
        }
    }

}