package com.project.devlog.global.response.dto;

public record PageInfo(
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast
) {
}
