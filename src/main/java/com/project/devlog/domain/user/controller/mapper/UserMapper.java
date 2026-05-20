package com.project.devlog.domain.user.controller.mapper;

import com.project.devlog.domain.user.dto.response.UserIdResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserIdResponse toIdDto(Long userId) {
        return new UserIdResponse(userId);
    }
}
