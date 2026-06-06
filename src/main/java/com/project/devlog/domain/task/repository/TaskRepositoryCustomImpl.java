package com.project.devlog.domain.task.repository;

import static com.project.devlog.domain.task.entity.QTask.task;
import static com.project.devlog.domain.user.entity.QUser.user;

import com.project.devlog.domain.task.dto.request.TaskSearchCondition;
import com.project.devlog.domain.task.entity.Task;
import com.project.devlog.domain.task.entity.enums.TaskPriority;
import com.project.devlog.domain.task.entity.enums.TaskStatus;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryCustomImpl implements TaskRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Task> searchUserProjects(Long projectId, TaskSearchCondition condition, Pageable pageable) {

        List<Task> content = queryFactory.selectFrom(task)
                .leftJoin(task.assignee, user).fetchJoin()
                .where(
                        task.project.id.eq(projectId),
                        task.isDeleted.isFalse(),
                        titleContains(condition.title()),
                        assigneeIdEq(condition.assigneeId()),
                        statusEq(condition.status()),
                        priorityEq(condition.priority())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getDueDateOrderSpecifier(pageable.getSort()))
                .fetch();

        Long total = queryFactory
                .select(task.count())
                .from(task)
                .leftJoin(task.assignee, user)
                .where(
                        task.project.id.eq(projectId),
                        task.isDeleted.isFalse(),
                        titleContains(condition.title()),
                        assigneeIdEq(condition.assigneeId()),
                        statusEq(condition.status()),
                        priorityEq(condition.priority())
                )
                .fetchOne();

        long totalCount = (total != null) ? total : 0L;

        return new PageImpl<>(content, pageable, totalCount);
    }

    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title) ? task.title.contains(title) : null;
    }

    private BooleanExpression assigneeIdEq(Long assigneeId) {
        return assigneeId != null && assigneeId != 0L ? task.assignee.id.eq(assigneeId) : null;
    }

    private BooleanExpression statusEq(String status) {
        if (!StringUtils.hasText(status) || "ALL".equalsIgnoreCase(status)) {
            return null;
        }
        return task.status.eq(TaskStatus.valueOf(status.toUpperCase()));
    }

    private BooleanExpression priorityEq(String priority) {
        if (!StringUtils.hasText(priority) || "ALL".equalsIgnoreCase(priority)) {
            return null;
        }
        return task.priority.eq(TaskPriority.valueOf(priority.toUpperCase()));
    }

    private OrderSpecifier<?> getDueDateOrderSpecifier(Sort sort) {

        if (sort == null || sort.isUnsorted()) {
            return new OrderSpecifier<>(Order.ASC, task.dueDate);
        }

        Sort.Order order = sort.iterator().next();
        Order direction = order.isAscending() ? Order.ASC : Order.DESC;

        return new OrderSpecifier<>(direction, task.dueDate);
    }

}
