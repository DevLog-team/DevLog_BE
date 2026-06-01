package com.project.devlog.domain.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.project.devlog.domain.project.dto.request.InviteMembersRequest;
import com.project.devlog.domain.project.dto.response.InviteMembersResponse;
import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.project.entity.ProjectUser;
import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.project.devlog.domain.project.mapper.ProjectMapper;
import com.project.devlog.domain.project.mock.ProjectMock;
import com.project.devlog.domain.project.repository.ProjectRepository;
import com.project.devlog.domain.project.repository.ProjectUserRepository;
import com.project.devlog.domain.user.entity.User;
import com.project.devlog.domain.user.repository.UserRepository;
import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.errorcode.ProjectErrorCode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMapper projectMapper;

    private final ProjectMock projectMock = new ProjectMock();

    @Nested
    @DisplayName("팀원 초대 요청 시")
    class InviteMembers {

        @Test
        @DisplayName("예외: 존재하지 않는 프로젝트 ID인 경우 초대 도중 PROJECT_NOT_FOUND 발생한다")
        void fail_projectNotFound() {
            // given
            Long projectId = 1L;
            InviteMembersRequest request = new InviteMembersRequest(List.of("success@test.com"));

            given(projectRepository.findProjectByIdAndIsDeletedFalse(projectId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> projectService.inviteMembers(projectId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ProjectErrorCode.PROJECT_NOT_FOUND.getMessage());

            verify(projectUserRepository, never()).save(any(ProjectUser.class));
        }

        @Test
        @DisplayName("성공: 다양한 상태의 이메일 목록(성공, 미가입, 이미 참여)을 입력받아 각각 분류하여 매퍼 결과를 반환한다")
        void success_processMultipleScenarios() {
            // given
            Project project = projectMock.domainMock(ProjectStatus.ACTIVE);
            Long projectId = project.getId();

            String successEmail = "success@test.com";
            String notFoundEmail = "notfound@test.com";
            String alreadyMemberEmail = "already@test.com";

            InviteMembersRequest request = new InviteMembersRequest(
                    List.of(successEmail, notFoundEmail, alreadyMemberEmail)
            );

            User targetUser = User.builder().id(2L).email(successEmail).build();
            User alreadyUser = User.builder().id(3L).email(alreadyMemberEmail).build();

            given(projectRepository.findProjectByIdAndIsDeletedFalse(projectId))
                    .willReturn(Optional.of(project));

            given(userRepository.findByEmailAndIsDeletedFalse(successEmail)).willReturn(Optional.of(targetUser));
            given(userRepository.findByEmailAndIsDeletedFalse(notFoundEmail)).willReturn(Optional.empty());
            given(userRepository.findByEmailAndIsDeletedFalse(alreadyMemberEmail)).willReturn(Optional.of(alreadyUser));

            given(projectUserRepository.existsByProjectIdAndUserIdAndIsDeletedFalse(projectId, targetUser.getId()))
                    .willReturn(false);
            given(projectUserRepository.existsByProjectIdAndUserIdAndIsDeletedFalse(projectId, alreadyUser.getId()))
                    .willReturn(true);

            InviteMembersResponse expectedResponse = new InviteMembersResponse(
                    List.of(successEmail),
                    List.of(notFoundEmail + " (존재하지 않는 회원)", alreadyMemberEmail + " (이미 참여 중인 팀원)")
            );
            given(projectMapper.toInviteMembersResponse(anyList(), anyList()))
                    .willReturn(expectedResponse);

            // when
            InviteMembersResponse actualResponse = projectService.inviteMembers(projectId, request);

            // then
            assertThat(actualResponse).isNotNull();
            assertThat(actualResponse.successEmails()).hasSize(1).contains(successEmail);
            assertThat(actualResponse.failedEmails()).hasSize(2)
                    .contains(notFoundEmail + " (존재하지 않는 회원)")
                    .contains(alreadyMemberEmail + " (이미 참여 중인 팀원)");

            verify(projectUserRepository, times(1)).save(any(ProjectUser.class));
        }
    }
}