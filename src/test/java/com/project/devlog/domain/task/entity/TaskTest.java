package com.project.devlog.domain.task.entity;

import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.task.entity.enums.TaskPriority;
import com.project.devlog.domain.task.entity.enums.TaskStatus;
import com.project.devlog.domain.user.entity.User;
import com.project.devlog.domain.user.entity.enums.UserRole;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    @Nested
    @DisplayName("작업 기본정보 수정 테스트")
    class UpdateBasicInfo {

        @Test
        @DisplayName("성공: 새로운 제목과 설명이 주어지면 작업 정보가 변경된다")
        void success_updateTitleAndDescription() {
            // given
            User mockAssignee = Mockito.mock(User.class);
            Project mockProject = Mockito.mock(Project.class);

            Task task = Task.builder()
                    .id(1L)
                    .title("기존 제목")
                    .description("기존 설명")
                    .status(TaskStatus.TODO)
                    .priority(TaskPriority.HIGH)
                    .dueDate(LocalDate.now().plusDays(7))
                    .assignee(mockAssignee)
                    .project(mockProject)
                    .build();

            String newTitle = "수정된 새로운 제목";
            String newDescription = "수정된 새로운 설명";

            // when
            task.update(newTitle, newDescription);

            // then
            assertThat(task.getTitle()).isEqualTo(newTitle);
            assertThat(task.getDescription()).isEqualTo(newDescription);
        }
    }

    @Nested
    @DisplayName("작업 상태 변경 테스트")
    class ChangeStatus {

        @Test
        @DisplayName("성공: 상태를 DONE이 아닌 다른 상태로 변경하면 completedAt은 null을 유지한다")
        void success_changeStatusToInProgress() {
            // given
            Task task = Task.builder()
                    .title("테스트 작업")
                    .status(TaskStatus.TODO)
                    .build();

            // when
            task.changeStatus(TaskStatus.IN_PROGRESS);

            // then
            assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            assertThat(task.getCompletedAt()).isNull();
        }

        @Test
        @DisplayName("성공: 상태를 DONE으로 변경하면 completedAt에 현재 시간이 기록된다")
        void success_changeStatusToDone() {
            // given
            Task task = Task.builder()
                    .title("테스트 작업")
                    .status(TaskStatus.TODO)
                    .build();

            LocalDateTime testStartTime = LocalDateTime.now();

            // when
            task.changeStatus(TaskStatus.DONE);

            // then
            assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE);
            assertThat(task.getCompletedAt()).isAfterOrEqualTo(testStartTime);
        }

        @Test
        @DisplayName("성공: DONE 상태에서 다시 다른 상태로 변경하면 completedAt이 null로 초기화된다")
        void success_changeStatusFromDoneToTodo() {
            // given
            Task task = Task.builder()
                    .title("완료되었던 작업")
                    .status(TaskStatus.DONE)
                    .build();

            task.changeStatus(TaskStatus.DONE);
            assertThat(task.getCompletedAt()).isNotNull();

            // when
            task.changeStatus(TaskStatus.TODO);

            // then
            assertThat(task.getStatus()).isEqualTo(TaskStatus.TODO);
            assertThat(task.getCompletedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("작업 우선순위 변경 테스트")
    class ChangePriority {

        @Test
        @DisplayName("성공: 우선순위 값을 변경한다")
        void success_changePriority() {
            // given
            User mockAssignee = Mockito.mock(User.class);
            Project mockProject = Mockito.mock(Project.class);

            Task task = Task.builder()
                    .id(1L)
                    .title("기존 제목")
                    .description("기존 설명")
                    .status(TaskStatus.TODO)
                    .priority(TaskPriority.HIGH)
                    .dueDate(LocalDate.now().plusDays(7))
                    .assignee(mockAssignee)
                    .project(mockProject)
                    .build();

            TaskPriority newPriority = TaskPriority.MEDIUM;

            // when
            task.changePriority(newPriority);

            // then
            assertThat(task.getPriority()).isEqualTo(newPriority);
        }
    }

    @Nested
    @DisplayName("작업 담당자 변경 테스트")
    class ChangeAssignee {

        @Test
        @DisplayName("성공: 담당자를 변경한다")
        void success_changeAssignee() {
            // given
            User beforeAssignee = User.builder()
                    .id(1L)
                    .email("이메일1")
                    .password("비밀번호1")
                    .name("이전 담당자")
                    .role(UserRole.USER)
                    .build();

            User afterAssignee = User.builder()
                    .id(1L)
                    .email("이메일2")
                    .password("비밀번호2")
                    .name("변경된 담당자")
                    .role(UserRole.USER)
                    .build();

            Project mockProject = Mockito.mock(Project.class);

            Task task = Task.builder()
                    .id(1L)
                    .title("기존 제목")
                    .description("기존 설명")
                    .status(TaskStatus.TODO)
                    .priority(TaskPriority.HIGH)
                    .dueDate(LocalDate.now().plusDays(7))
                    .assignee(beforeAssignee)
                    .project(mockProject)
                    .build();

            // when
            task.changeAssignee(afterAssignee);

            // then
            assertThat(task.getAssignee()).isEqualTo(afterAssignee);
        }
    }
}