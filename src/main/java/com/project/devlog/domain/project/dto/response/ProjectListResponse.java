package com.project.devlog.domain.project.dto.response;

import com.project.devlog.global.response.dto.PageInfo;
import java.util.List;

public record ProjectListResponse(
        List<ProjectSummaryResponse> content,
        PageInfo pageInfo
) { }
