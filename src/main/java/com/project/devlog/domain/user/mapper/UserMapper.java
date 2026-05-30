package com.project.devlog.domain.user.mapper;

import com.project.devlog.domain.user.dto.request.SignupRequest;
import com.project.devlog.domain.user.dto.response.UserIdResponse;
import com.project.devlog.domain.user.entity.User;
import com.project.devlog.domain.user.entity.enums.UserRole;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserIdResponse toIdDto(Long userId) {
        return new UserIdResponse(userId);
    }

    public User toUser(SignupRequest requestDto, String encodedPassword) {
        return User.builder()
                .email(requestDto.email())
                .password(encodedPassword)
                .name(requestDto.name())
                .role(UserRole.USER)
                .build();
    }
}
