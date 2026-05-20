package com.project.devlog.domain.user.service;

import com.project.devlog.domain.user.dto.request.SignupRequest;
import com.project.devlog.domain.user.entity.User;
import com.project.devlog.domain.user.entity.enums.UserRole;
import com.project.devlog.domain.user.repository.UserRepository;
import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.errorcode.UserErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signup(@Valid SignupRequest requestDto) {
        validateDuplicateEmail(requestDto.email());

        String encodedPassword = passwordEncoder.encode(requestDto.password());
        User user = User.builder()
                .email(requestDto.email())
                .password(encodedPassword)
                .name(requestDto.name())
                .role(UserRole.USER)
                .build();

        return userRepository.save(user).getId();
    }

    private void validateDuplicateEmail(String email) {
        userRepository.findByEmailAndIsDeletedFalse(email)
                .ifPresent(user -> {
                    throw new BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS);
                });
    }
}
