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

}