package com.project.devlog.domain.user.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

import com.project.devlog.domain.user.dto.request.SignupRequest;
import com.project.devlog.domain.user.entity.User;
import com.project.devlog.domain.user.mock.UserMock;
import com.project.devlog.domain.user.repository.UserRepository;
import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.errorcode.UserErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService sut;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    UserMock mock;

    @BeforeEach
    void setUp() { mock = new UserMock(passwordEncoder); }

    @Test
    @DisplayName("회원 가입 테스트: 성공")
    void signup_success_test() throws Exception {
        // given
        User savedUser = mock.domainMock();
        SignupRequest requestDto = mock.signupMock();

        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        Long userId = sut.signup(requestDto);

        // then
        Assertions.assertThat(userId).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("회원 가입 테스트: 실패[이미 존재하는 이메일일 경우]")
    void signup_fail_when_email_is_existed() throws Exception {
        // given
        SignupRequest requestDto = mock.signupMock();

        doThrow(new BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS))
                .when(userRepository).save(any(User.class));

        // when & then
        assertThatThrownBy(() -> sut.signup(requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
    }

}