package com.project.devlog.domain.user.mock;

import com.project.devlog.domain.user.dto.request.SignupRequest;
import com.project.devlog.domain.user.entity.User;
import com.project.devlog.domain.user.entity.enums.UserRole;
import com.project.devlog.global.security.dto.LoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMock {

    private final PasswordEncoder encoder;

    private final Long id = 1L;
    private final String email = "test@naver.com";
    private final String password = "testpw1234";

    private final String wrongPassword = "wrongpw1234";
    private final String notTwoTypePassword = "test";
    private final String lineWrongPassword = "t";

    private final String name = "test";
    private final UserRole userRole = UserRole.USER;

    public UserMock(PasswordEncoder encoder) { this.encoder = encoder; }

    public User domainMock() {
        return User.builder()
                .id(id)
                .email(email)
                .password(encoder.encode(password))
                .name(name)
                .role(userRole)
                .build();
    }

    public SignupRequest signupMock() { return new SignupRequest(email, password, name); }

    public SignupRequest signupNotTwoTypePasswordMock() { return new SignupRequest(email, notTwoTypePassword, name); }

    public SignupRequest signupLineWrongPasswordMock() { return new SignupRequest(email, lineWrongPassword, name); }

    public LoginRequest loginMock() { return new LoginRequest(email, password); }

    public LoginRequest wrongLoginMock() { return new LoginRequest(email, wrongPassword); }

    public String getBeforeEncodedPw() {
        return this.password;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
