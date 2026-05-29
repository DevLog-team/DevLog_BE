package com.project.devlog.domain.project.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.errorcode.ProjectErrorCode;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProjectTest {

    @Nested
    @DisplayName("프로젝트 생성 시")
    class project_create {
        @Test
        @DisplayName("성공: 프로젝트가 성공적으로 생성된다 (마감일이 시작일 이후)")
        void success() {
            // given
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now().plusDays(1);

            // when
            Project project = Project.builder()
                    .title("테스트 프로젝트")
                    .description("테스트 입니다.")
                    .status(ProjectStatus.ACTIVE)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            // then
            assertThat(project).isNotNull();
            assertThat(project.getStartDate()).isEqualTo(startDate);
            assertThat(project.getEndDate()).isEqualTo(endDate);
        }

        @Test
        @DisplayName("실패: 프로젝트 마감일이 시작일보다 빠를 경우")
        void fail_when_endDate_before_startDate() {
            // given
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now().minusDays(1);

            // when & then
            assertThatThrownBy(() -> Project.builder()
                    .title("테스트 프로젝트")
                    .description("테스트 입니다.")
                    .status(ProjectStatus.ACTIVE)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build())
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ProjectErrorCode.INVALID_PROJECT_DATE.getMessage());
        }
    }

    @Nested
    @DisplayName("프로젝트 정보 수정 시")
    class project_update {

        @Test
        @DisplayName("성공: 모든 수정 데이터가 정상적일 경우 필드가 성공적으로 변경된다")
        void success() {
            // given
            Project project = Project.builder()
                    .title("기존 제목")
                    .description("기존 설명")
                    .status(ProjectStatus.ACTIVE)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusMonths(1))
                    .build();

            String updatedTitle = "수정된 제목";
            String updatedDescription = "수정된 설명";
            ProjectStatus updatedStatus = ProjectStatus.COMPLETED;
            LocalDate updatedStartDate = LocalDate.now().plusDays(5);
            LocalDate updatedEndDate = LocalDate.now().plusMonths(2);

            // when
            project.update(updatedTitle, updatedDescription, updatedStatus, updatedStartDate, updatedEndDate);

            // then
            assertThat(project.getTitle()).isEqualTo(updatedTitle);
            assertThat(project.getDescription()).isEqualTo(updatedDescription);
            assertThat(project.getStatus()).isEqualTo(updatedStatus);
            assertThat(project.getStartDate()).isEqualTo(updatedStartDate);
            assertThat(project.getEndDate()).isEqualTo(updatedEndDate);
        }

        @Test
        @DisplayName("실패: 수정하려는 마감일이 시작일보다 빠를 경우 예외가 발생한다")
        void fail_when_updated_endDate_before_startDate() {
            // given
            Project project = Project.builder()
                    .title("기존 제목")
                    .description("기존 설명")
                    .status(ProjectStatus.ACTIVE)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusMonths(1))
                    .build();

            // 시작일보다 하루 빠른 날짜를 종료일로 설정
            LocalDate invalidStartDate = LocalDate.now().plusDays(10);
            LocalDate invalidEndDate = LocalDate.now().plusDays(9);

            // when & then
            assertThatThrownBy(() -> project.update(
                    "수정 제목", "수정 설명", ProjectStatus.ACTIVE, invalidStartDate, invalidEndDate
            ))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ProjectErrorCode.INVALID_PROJECT_DATE.getMessage());
        }
    }
}