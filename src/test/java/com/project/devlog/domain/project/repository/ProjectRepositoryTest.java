package com.project.devlog.domain.project.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.devlog.domain.project.dto.request.ProjectSearchCondition;
import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.project.devlog.domain.project.entity.projection.ProjectListProjection;
import com.project.devlog.global.config.JpaConfig;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@DataJpaTest
@Import({ProjectRepositoryCustomImpl.class, JpaConfig.class})
@Transactional
class ProjectRepositoryTest {

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
    private ProjectRepository sut;

    @Test
    @DisplayName("사용자 프로젝트 목록 조회 - 상태 ALL 조건 및 페이지네이션 테스트")
    void search_projects_with_status_all_and_pagination_test() {
        // given
        Long userId = 1L;
        ProjectSearchCondition condition = new ProjectSearchCondition(null, "ALL");
        Pageable pageable = PageRequest.of(0, 2);

        // when
        Page<ProjectListProjection> result = sut.searchUserProjects(userId, condition, pageable);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(4); // 총 개수는 4개여야 함
        assertThat(result.getTotalPages()).isEqualTo(2);    // 페이지당 2개씩이니 총 2페이지
        assertThat(result.getContent()).hasSize(2);         // 현재 페이지 컨텐츠 개수 2개 확인

        // 다른 유저(2번)의 프로젝트는 섞여있지 않은지 검증
        List<String> titles = result.getContent().stream().map(ProjectListProjection::title).toList();
        assertThat(titles).doesNotContain("타 유저 프로젝트");
    }

    @Test
    @DisplayName("사용자 프로젝트 목록 조회 - 특정 상태(ACTIVE) 필터링 테스트")
    void search_projects_with_specific_status_test() {
        // given
        Long userId = 1L;
        ProjectSearchCondition condition = new ProjectSearchCondition(null, "ACTIVE");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ProjectListProjection> result = sut.searchUserProjects(userId, condition, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2); // 더미데이터 중 유저1의 ACTIVE는 2개

        ProjectListProjection project1 = result.getContent().get(0);
        ProjectListProjection project2 = result.getContent().get(1);

        assertThat(project1.status()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(project2.status()).isEqualTo(ProjectStatus.ACTIVE);
    }

    @Test
    @DisplayName("사용자 프로젝트 목록 조회 - 제목 키워드 포함 동적 필터링 테스트")
    void search_projects_with_title_contains_test() {
        // given
        Long userId = 1L;
        ProjectSearchCondition condition = new ProjectSearchCondition("프로젝트", "ALL");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ProjectListProjection> result = sut.searchUserProjects(userId, condition, pageable);

        // then
        // '스프링부트 프로젝트', '리액트 클론 프로젝트', '인프라 구축 프로젝트' 총 3개 ('보안 고도화 작업' 제외)
        assertThat(result.getTotalElements()).isEqualTo(3);

        boolean hasSecurityProject = result.getContent().stream()
                .anyMatch(p -> p.title().contains("보안 고도화"));
        assertThat(hasSecurityProject).isFalse();
    }
}