package com.project.devlog.domain.project.dto.response;

import java.util.List;

public record ProjectMembersResponse(
        List<ProjectMember> members
) {

    public record ProjectMember(
            Long userId,
            String name
    ) {}

}
