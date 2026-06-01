package com.project.devlog.domain.project.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record InviteMembersRequest(
        @NotEmpty(message = "초대할 팀원의 이메일은 최소 1개 이상이여야 합니다.")
        @Size(max = 10, message = "한 번에 최대 10명까지만 초대할 수 있습니다.")
        List<String> emails
) {
}
