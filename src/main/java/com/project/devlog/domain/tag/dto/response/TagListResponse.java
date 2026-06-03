package com.project.devlog.domain.tag.dto.response;

import java.util.List;

public record TagListResponse(
        List<TagResponse> tags
) {
}
