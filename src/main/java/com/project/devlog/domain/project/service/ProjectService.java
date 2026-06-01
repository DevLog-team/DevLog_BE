package com.project.devlog.domain.project.service;

import com.project.devlog.domain.project.dto.request.CreateProjectRequest;
import com.project.devlog.domain.project.dto.request.InviteMembersRequest;
import com.project.devlog.domain.project.dto.request.ProjectSearchCondition;
import com.project.devlog.domain.project.dto.request.UpdateProjectRequest;
import com.project.devlog.domain.project.dto.response.InviteMembersResponse;
import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.project.entity.ProjectUser;
import com.project.devlog.domain.project.entity.enums.ProjectUserRole;
import com.project.devlog.domain.project.entity.projection.ProjectListProjection;
import com.project.devlog.domain.project.entity.projection.ProjectProjection;
import com.project.devlog.domain.project.mapper.ProjectMapper;
import com.project.devlog.domain.project.repository.ProjectRepository;
import com.project.devlog.domain.project.repository.ProjectUserRepository;
import com.project.devlog.domain.user.entity.User;
import com.project.devlog.domain.user.repository.UserRepository;
import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.errorcode.ProjectErrorCode;
import com.project.devlog.global.exception.errorcode.UserErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public Long create(Long userId, CreateProjectRequest request) {
        User user = findUserById(userId);
        Project project = projectMapper.toProject(request);
        ProjectUser projectUser = projectMapper.toProjectUser(project, user, ProjectUserRole.OWNER);

        projectRepository.save(project);
        projectUserRepository.save(projectUser);

        return project.getId();
    }

    private User findUserById(Long userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_EXIST_USER));
    }

    public Page<ProjectListProjection> getList(Long userId, ProjectSearchCondition condition, Pageable pageable) {
        return projectRepository.searchUserProjects(userId, condition, pageable);
    }

    public ProjectProjection getDetail(Long userId, Long projectId) {
        return projectRepository.findProjectDetail(userId, projectId)
                .orElseThrow(() -> new BusinessException(ProjectErrorCode.PROJECT_NOT_FOUND));
    }

    @Transactional
    public Long update(Long projectId, UpdateProjectRequest request) {
        Project project = findProjectById(projectId);
        project.update(request.title(), request.description(), request.status(), request.startDate(),
                request.endDate());
        return project.getId();
    }

    private Project findProjectById(Long projectId) {
        return projectRepository.findProjectByIdAndIsDeletedFalse(projectId)
                .orElseThrow(() -> new BusinessException(ProjectErrorCode.PROJECT_NOT_FOUND));

    }

    @Transactional
    public void delete(Long projectId) {
        Project project = findProjectById(projectId);
        List<ProjectUser> projectUsers = findProjectUserByProjectId(project.getId());
        project.delete();
        projectUsers.forEach(ProjectUser::delete);
    }

    private List<ProjectUser> findProjectUserByProjectId(Long projectId) {
        return projectUserRepository.findByProjectIdAndIsDeletedFalse(projectId);
    }

    @Transactional
    public InviteMembersResponse inviteMembers(Long projectId, InviteMembersRequest request) {
        Project project = findProjectById(projectId);

        List<String> successEmails = new ArrayList<>();
        List<String> failedEmails = new ArrayList<>();

        for (String email : request.emails()) {
            processInvitation(project, email, successEmails, failedEmails);
        }

        return projectMapper.toInviteMembersResponse(successEmails, failedEmails);
    }

    private void processInvitation(Project project, String email, List<String> successEmails, List<String> failedEmails) {
        Optional<User> userOptional = userRepository.findByEmailAndIsDeletedFalse(email);

        if (userOptional.isEmpty()) {
            failedEmails.add(email + " (존재하지 않는 회원)");
            return;
        }

        User targetUser = userOptional.get();

        boolean isAlreadyMember = projectUserRepository.existsByProjectIdAndUserIdAndIsDeletedFalse(
                project.getId(), targetUser.getId()
        );
        if (isAlreadyMember) {
            failedEmails.add(email + " (이미 참여 중인 팀원)");
            return;
        }

        ProjectUser projectUser = ProjectUser.builder()
                .project(project)
                .user(targetUser)
                .role(ProjectUserRole.MEMBER)
                .build();

        projectUserRepository.save(projectUser);
        successEmails.add(email);
    }
}
