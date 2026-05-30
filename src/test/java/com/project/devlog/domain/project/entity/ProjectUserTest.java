package com.project.devlog.domain.project.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.project.devlog.domain.project.entity.enums.ProjectUserRole;
import com.project.devlog.domain.user.entity.User;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProjectUserTest {

    @Nested
    @DisplayName("프로젝트 멤버 삭제 시")
    class projectUser_delete {

        @Test
        @DisplayName("성공: 삭제 메서드 호출 시 isDeleted가 true로 변경된다")
        void success() {
            // given
            Project project = Project.builder()
                    .title("테스트 프로젝트")
                    .status(ProjectStatus.ACTIVE)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusDays(1))
                    .build();

            User user = User.builder()
                    .id(1L)
                    .email("test@devlog.com")
                    .build();

            ProjectUser projectUser = ProjectUser.builder()
                    .project(project)
                    .user(user)
                    .role(ProjectUserRole.MEMBER)
                    .build();

            // when
            projectUser.delete();

            // then
            assertThat(projectUser.isDeleted()).isTrue();
        }
    }
}