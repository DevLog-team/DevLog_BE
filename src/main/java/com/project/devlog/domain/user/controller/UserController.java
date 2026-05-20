package com.project.devlog.domain.user.controller;

import com.project.devlog.domain.user.controller.mapper.UserMapper;
import com.project.devlog.domain.user.dto.request.SignupRequest;
import com.project.devlog.domain.user.dto.response.UserIdResponse;
import com.project.devlog.domain.user.service.UserService;
import com.project.devlog.global.util.UrlCreator;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private static final String DEFAULT_URL = "/api/users";
    private final UserService userService;
    private final UserMapper userMapper;


    @PostMapping("/api/signup")
    public ResponseEntity<UserIdResponse> signup(@Valid @RequestBody SignupRequest requestDto) {
        Long userId = userService.signup(requestDto);
        URI location = UrlCreator.createUri(DEFAULT_URL, userId);
        return ResponseEntity.created(location).body(userMapper.toIdDto(userId));
    }

}
