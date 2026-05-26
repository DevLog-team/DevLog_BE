package com.project.devlog.domain.project.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.devlog.domain.project.dto.request.CreateProjectRequest;
import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.project.devlog.domain.project.mock.ProjectMock;
import com.project.devlog.domain.project.service.ProjectService;
import com.project.devlog.global.config.AuthTestConfig;
import com.project.devlog.global.config.SecurityConfig;
import com.project.devlog.global.security.annotation.MockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ProjectController.class)
@Import({AuthTestConfig.class, SecurityConfig.class, ProjectMock.class})
@AutoConfigureRestDocs
class ProjectControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProjectMock projectMock;

    @Autowired
    private ProjectService projectService;

    @Nested
    @DisplayName("프로젝트 생성 테스트")
    class create {
        @Test
        @DisplayName("성공: project, projectUser 생성 후 projectId 반환")
        @MockCustomUser
        void success() throws Exception {
            // given
            CreateProjectRequest requestDto = projectMock.createRequestMock();
            Project mockProject = projectMock.domainMock(ProjectStatus.ACTIVE);
            String content = objectMapper.writeValueAsString(requestDto);

            given(projectService.create(anyLong(), any(CreateProjectRequest.class)))
                    .willReturn(mockProject.getId());

            // when
            ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/project")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(content));

            // then
            perform
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.body.projectId").isNumber())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andDo(document("프로젝트 생성 성공",
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Project")
                                                    .description("프로젝트 생성 API")
                                                    .requestSchema(Schema.schema("CreateProjectRequest"))
                                                    .requestFields(
                                                            fieldWithPath("title").type(JsonFieldType.STRING)
                                                                    .description("프로젝트 제목"),
                                                            fieldWithPath("description").type(JsonFieldType.STRING)
                                                                    .description("프로젝트 간략 설명"),
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("프로젝트 상태"),
                                                            fieldWithPath("role").type(JsonFieldType.STRING)
                                                                    .description("프로젝트 사용자 권한"),
                                                            fieldWithPath("startDate").type(JsonFieldType.STRING)
                                                                    .description("프로젝트 시작일"),
                                                            fieldWithPath("endDate").type(JsonFieldType.STRING)
                                                                    .description("프로젝트 종료일")
                                                    )
                                                    .responseSchema(Schema.schema("CreateProjectResponse"))
                                                    .responseFields(
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("응답 상태"),
                                                            fieldWithPath("body.projectId").type(JsonFieldType.NUMBER)
                                                                    .description("프로젝트 ID"),
                                                            fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                                    .description("응답 시간"))
                                                    .build()
                                    )
                            )
                    );
        }
    }
}