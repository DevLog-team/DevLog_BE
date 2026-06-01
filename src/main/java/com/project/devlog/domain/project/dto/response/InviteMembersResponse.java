package com.project.devlog.domain.project.dto.response;

import java.util.List;

public record InviteMembersResponse(
        List<String> successEmails,
        List<String> failedEmails
) {
    public static InviteMembersResponse of(List<String> successEmails, List<String> failedEmails) {
        return new InviteMembersResponse(successEmails, failedEmails);
    }
}
