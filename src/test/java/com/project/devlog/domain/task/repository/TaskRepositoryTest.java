package com.project.devlog.domain.task.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.devlog.domain.task.dto.request.TaskSearchCondition;
import com.project.devlog.domain.task.entity.Task;
import com.project.devlog.domain.task.entity.enums.TaskStatus;
import com.project.devlog.global.config.JpaConfig;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
@Testcontainers
@DataJpaTest
@Import({TaskRepositoryCustomImpl.class, JpaConfig.class})
@Transactional
class TaskRepositoryTest {

    @Container
    static final OracleContainer container = new OracleContainer(
            DockerImageName.parse("gvenzl/oracle-free:slim")
                    .asCompatibleSubstituteFor("gvenzl/oracle-xe")
    )
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword")
            .withInitScript("init_db.sql");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.driver-class-name", container::getDriverClassName);
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.OracleDialect");
    }

    @Autowired
    private TaskRepository sut;

    private final Long projectId = 1L;

    @Nested
    @DisplayName("작업 동적 조회 및 검색 테스트")
    class SearchTasks {

        @Test
        @DisplayName("성공: 조건 없이 검색 시(ALL 또는 기본값) 삭제되지 않은 해당 프로젝트의 모든 작업 반환")
        void searchAllTasksSuccess() {
            // given
            TaskSearchCondition condition = new TaskSearchCondition("", 0L, "ALL", "ALL");
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "dueDate"));

            // when
            Page<Task> result = sut.searchUserProjects(projectId, condition, pageable);

            // then
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getContent()).extracting("title")
                    .containsExactly("회원가입 기능", "로그인 API 구현", "알림 시스템 설계");
        }

        @Test
        @DisplayName("성공: 제목 및 작업 상태 필터 동적 매칭")
        void searchByTitleAndStatusSuccess() {
            // given
            TaskSearchCondition condition = new TaskSearchCondition("API", 0L, "TODO", "ALL");
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Task> result = sut.searchUserProjects(projectId, condition, pageable);

            // then
            assertThat(result.getTotalElements()).isEqualTo(1);
            Task matchedTask = result.getContent().get(0);
            assertThat(matchedTask.getTitle()).contains("로그인 API 구현");
            assertThat(matchedTask.getStatus()).isEqualTo(TaskStatus.TODO);
        }

        @Test
        @DisplayName("성공: 특정 담당자(Assignee) 조건 필터링 검증")
        void searchByAssigneeSuccess() {
            // given
            Long developerAId = 1L; // 개발자A 식별 ID
            TaskSearchCondition condition = new TaskSearchCondition("", developerAId, "ALL", "ALL");
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<Task> result = sut.searchUserProjects(projectId, condition, pageable);

            // then
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent()).allMatch(task -> task.getAssignee().getId().equals(developerAId));
        }

        @Test
        @DisplayName("성공: 마감일 기준 내림차순(DESC) 정렬 옵션 검증")
        void searchWithDueDateDescSuccess() {
            // given
            TaskSearchCondition condition = new TaskSearchCondition("", 0L, "ALL", "ALL");

            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dueDate"));

            // when
            Page<Task> result = sut.searchUserProjects(projectId, condition, pageable);

            // then
            List<Task> content = result.getContent();
            assertThat(content).hasSize(3);

            assertThat(content.get(0).getDueDate()).isEqualTo(java.time.LocalDate.of(2026, 6, 20));
            assertThat(content.get(1).getDueDate()).isEqualTo(java.time.LocalDate.of(2026, 6, 15));
            assertThat(content.get(2).getDueDate()).isEqualTo(java.time.LocalDate.of(2026, 6, 10));
        }
    }
}