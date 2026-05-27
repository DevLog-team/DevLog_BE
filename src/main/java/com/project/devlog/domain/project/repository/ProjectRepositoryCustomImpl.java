package com.project.devlog.domain.project.repository;

import static com.project.devlog.domain.project.entity.QProject.project;
import static com.project.devlog.domain.project.entity.QProjectUser.projectUser;

import com.project.devlog.domain.project.dto.request.ProjectSearchCondition;
import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.project.devlog.domain.project.entity.projection.ProjectListProjection;
import com.project.devlog.domain.project.entity.projection.ProjectProjection;
import com.project.devlog.domain.project.entity.projection.QProjectListProjection;
import com.project.devlog.domain.project.entity.projection.QProjectProjection;
import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.errorcode.ProjectErrorCode;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProjectListProjection> searchUserProjects(Long userId, ProjectSearchCondition condition,
                                                          Pageable pageable) {
        List<ProjectListProjection> content = queryFactory.select(
                        new QProjectListProjection(
                                project.id,
                                project.title,
                                project.description,
                                project.status,
                                project.endDate,
                                // TODO 작업 기능 추가 이후 개발 예정 (임시 데이터)
                                Expressions.constant(0L),
                                Expressions.constant(0L),
                                Expressions.constant(0L)
                        ))
                .from(projectUser)
                .join(projectUser.project, project)
                .where(
                        projectUser.user.id.eq(userId),
                        projectUser.isDeleted.isFalse(),
                        project.isDeleted.isFalse(),
                        titleContains(condition.title()),
                        statusEq(condition.status())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(project.count())
                .from(projectUser)
                .join(projectUser.project, project)
                .where(
                        projectUser.user.id.eq(userId),
                        projectUser.isDeleted.isFalse(),
                        project.isDeleted.isFalse(),
                        titleContains(condition.title()),
                        statusEq(condition.status())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title) ? project.title.contains(title) : null;
    }

    private BooleanExpression statusEq(String statusStr) {
        if (!StringUtils.hasText(statusStr) || "ALL".equalsIgnoreCase(statusStr)) {
            return null;
        }
        try {
            ProjectStatus status = ProjectStatus.valueOf(statusStr.toUpperCase());
            return project.status.eq(status);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ProjectErrorCode.INVALID_PROJECT_STATUS);
        }
    }

    @Override
    public Optional<ProjectProjection> findProjectDetail(Long userId, Long projectId) {
        ProjectProjection result = queryFactory
                .select(new QProjectProjection(
                        project.id,
                        project.title,
                        project.description,
                        project.status,
                        project.startDate,
                        project.endDate,

                        JPAExpressions
                                .select(projectUser.count())
                                .from(projectUser)
                                .where(
                                        projectUser.project.id.eq(projectId),
                                        projectUser.isDeleted.isFalse()
                                ),

                        Expressions.constant(0L), // 총 작업 수 임시
                        Expressions.constant(0L), // 완료 작업 수 임시
                        Expressions.constant(0L), // 진행중 작업 수 임시
                        Expressions.constant(0L)  // 지연된 작업 수 임시

                ))
                .from(project)
                .join(projectUser).on(projectUser.project.id.eq(project.id))
                .where(
                        projectUser.user.id.eq(userId),
                        projectUser.isDeleted.isFalse(),
                        project.id.eq(projectId),
                        project.isDeleted.isFalse()
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
