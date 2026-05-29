package com.project.devlog.domain.project.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.devlog.domain.project.dto.request.CreateProjectRequest;
import com.project.devlog.domain.project.dto.request.ProjectSearchCondition;
import com.project.devlog.domain.project.dto.request.UpdateProjectRequest;
import com.project.devlog.domain.project.entity.Project;
import com.project.devlog.domain.project.entity.enums.ProjectStatus;
import com.project.devlog.domain.project.entity.projection.ProjectListProjection;
import com.project.devlog.domain.project.entity.projection.ProjectProjection;
import com.project.devlog.domain.project.mock.ProjectMock;
import com.project.devlog.domain.project.service.ProjectService;
import com.project.devlog.global.config.AuthTestConfig;
import com.project.devlog.global.config.SecurityConfig;
import com.project.devlog.global.security.annotation.MockCustomUser;
import com.project.devlog.global.security.evaluator.ProjectSecurityEvaluator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private ProjectSecurityEvaluator projectSecurityEvaluator;

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

    @Nested
    @DisplayName("프로젝트 목록 조회 테스트")
    class GetList {
        @Test
        @DisplayName("성공: 검색 조건 및 페이징이 반영된 프로젝트 목록 반환")
        @MockCustomUser
        void success() throws Exception {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // 🚀 ProjectMock을 활용하여 데이터 바인딩 통합 관리
            Page<ProjectListProjection> mockPage = projectMock.pageProjectionMock(pageable);

            given(projectService.getList(anyLong(), any(ProjectSearchCondition.class), any(Pageable.class)))
                    .willReturn(mockPage);

            // when
            ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/projects")
                    .param("title", "스프링부트")
                    .param("status", "ACTIVE") // 💡 특정 조건 필터링 검증용 파라미터 주입
                    .param("page", "0")
                    .param("size", "10")
                    .param("sort", "createdAt,desc")
                    .accept(MediaType.APPLICATION_JSON));

            // then
            perform
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.body.content").isArray())
                    .andExpect(jsonPath("$.body.pageInfo.currentPage").value(1))
                    .andExpect(jsonPath("$.body.pageInfo.pageSize").value(10))
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andDo(document("프로젝트 목록 조회 성공",
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Project")
                                                    .description("프로젝트 목록 조회 API(페이징 및 동적 검색을 지원)")
                                                    .queryParameters(
                                                            parameterWithName("title").description("프로젝트 제목 검색 키워드 (부분 일치)")
                                                                    .optional(),
                                                            // 💡 status 제약 조건 명시 추가
                                                            parameterWithName("status").description(
                                                                            "프로젝트 상태 필터링(ALL, ACTIVE, COMPLETED, ARCHIVED)").optional()
                                                                    .attributes(key("constraints").value(
                                                                            "ACTIVE, COMPLETED, ARCHIVED (ALL 혹은 미입력 시 전체 조회)")),
                                                            parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값: 0)")
                                                                    .optional(),
                                                            parameterWithName("size").description("한 페이지 당 조회 개수 (기본값: 10)")
                                                                    .optional(),
                                                            parameterWithName("sort").description(
                                                                    "정렬 기준 및 방향 (기본값: createdAt,desc)").optional()
                                                    )
                                                    .responseSchema(Schema.schema("ProjectListResponse"))
                                                    .responseFields(
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("응답 상태"),

                                                            // content 내부 배열 스펙 명시
                                                            fieldWithPath("body.content[]").type(JsonFieldType.ARRAY)
                                                                    .description("프로젝트 목록 데이터"),
                                                            fieldWithPath("body.content[].projectId").type(JsonFieldType.NUMBER)
                                                                    .description("프로젝트 고유 ID"),
                                                            fieldWithPath("body.content[].title").type(JsonFieldType.STRING)
                                                                    .description("프로젝트 제목"),
                                                            fieldWithPath("body.content[].description").type(
                                                                    JsonFieldType.STRING).description("프로젝트 상세 설명"),
                                                            fieldWithPath("body.content[].status").type(JsonFieldType.STRING)
                                                                    .description("프로젝트 상태 (ACTIVE, COMPLETED, ARCHIVED)"),
                                                            fieldWithPath("body.content[].endDate").type(JsonFieldType.STRING)
                                                                    .description("프로젝트 마감일 (YYYY-MM-DD)"),
                                                            fieldWithPath("body.content[].totalTaskCount").type(
                                                                    JsonFieldType.NUMBER).description("총 태스크 개수"),
                                                            fieldWithPath("body.content[].completedTaskCount").type(
                                                                    JsonFieldType.NUMBER).description("완료된 태스크 개수"),
                                                            fieldWithPath("body.content[].inProgressTaskCount").type(
                                                                    JsonFieldType.NUMBER).description("진행 중인 태스크 개수"),
                                                            fieldWithPath("body.content[].progressRate").type(
                                                                    JsonFieldType.NUMBER).description("프로젝트 전체 진척률 (%)"),

                                                            // pageInfo 페이징 데이터 스펙 명시
                                                            fieldWithPath("body.pageInfo").type(JsonFieldType.OBJECT)
                                                                    .description("페이징 메타데이터"),
                                                            fieldWithPath("body.pageInfo.currentPage").type(
                                                                    JsonFieldType.NUMBER).description("현재 페이지 번호 (1-indexed)"),
                                                            fieldWithPath("body.pageInfo.pageSize").type(JsonFieldType.NUMBER)
                                                                    .description("페이지 당 노출 데이터 개수"),
                                                            fieldWithPath("body.pageInfo.totalElements").type(
                                                                    JsonFieldType.NUMBER).description("총 데이터 개수"),
                                                            fieldWithPath("body.pageInfo.totalPages").type(JsonFieldType.NUMBER)
                                                                    .description("총 페이지 수"),
                                                            fieldWithPath("body.pageInfo.isFirst").type(JsonFieldType.BOOLEAN)
                                                                    .description("첫 페이지 여부"),
                                                            fieldWithPath("body.pageInfo.isLast").type(JsonFieldType.BOOLEAN)
                                                                    .description("마지막 페이지 여부"),

                                                            fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                                    .description("응답 시간")
                                                    )
                                                    .build()
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("프로젝트 단건 상세 조회 테스트")
    class GetDetail {
        @Test
        @DisplayName("성공: 프로젝트 ID로 상세 정보 및 작업 통계 데이터를 반환")
        @MockCustomUser
        void success() throws Exception {
            // given
            Long projectId = 1L;
            ProjectProjection mockProjection = projectMock.projectProjectionMock();

            given(projectService.getDetail(anyLong(), anyLong())).willReturn(mockProjection);

            // when
            ResultActions perform = mockMvc.perform(
                    RestDocumentationRequestBuilders.get("/api/projects/{projectId}", projectId)
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.body").isNotEmpty())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andDo(document("프로젝트 단건 조회 성공",
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Project")
                                                    .description("프로젝트 단건 조회 API")
                                                    .pathParameters(
                                                            parameterWithName("projectId").description("조회할 프로젝트의 고유 식별 ID")
                                                    )
                                                    .responseSchema(Schema.schema("ProjectResponse"))
                                                    .responseFields(
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("응답 상태"),

                                                            fieldWithPath("body.projectId").type(JsonFieldType.NUMBER)
                                                                    .description("프로젝트 고유 ID"),
                                                            fieldWithPath("body.title").type(JsonFieldType.STRING)
                                                                    .description("프로젝트 제목"),
                                                            fieldWithPath("body.description").type(JsonFieldType.STRING)
                                                                    .description("프로젝트 상세 설명"),
                                                            fieldWithPath("body.status").type(JsonFieldType.STRING)
                                                                    .description("프로젝트 진행 상태 (ACTIVE, COMPLETED, ARCHIVED)"),
                                                            fieldWithPath("body.startDate").type(JsonFieldType.STRING)
                                                                    .description("프로젝트 개시일 (YYYY-MM-DD)"),
                                                            fieldWithPath("body.endDate").type(JsonFieldType.STRING)
                                                                    .description("프로젝트 마감일 (YYYY-MM-DD)"),
                                                            fieldWithPath("body.memberCount").type(JsonFieldType.NUMBER)
                                                                    .description("프로젝트에 참가하고 있는 팀원 수"),
                                                            fieldWithPath("body.totalTaskCount").type(JsonFieldType.NUMBER)
                                                                    .description("등록된 전체 태스크 개수"),
                                                            fieldWithPath("body.completedTaskCount").type(JsonFieldType.NUMBER)
                                                                    .description("완료 처리된 태스크 개수"),
                                                            fieldWithPath("body.inProgressTaskCount").type(JsonFieldType.NUMBER)
                                                                    .description("현재 진행 중인 태스크 개수"),
                                                            fieldWithPath("body.delayedTaskCount").type(JsonFieldType.NUMBER)
                                                                    .description("기한이 지나 지연된 태스크 개수"),

                                                            fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                                    .description("응답 시간")
                                                    )
                                                    .build()
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("프로젝트 수정 테스트")
    class Update {
        @Test
        @DisplayName("성공: 프로젝트 OWNER 권한 검증 통과 후 성공적으로 프로젝트를 수정하고 ID를 반환한다")
        @MockCustomUser
        void success() throws Exception {
            // given
            Long projectId = 1L;
            UpdateProjectRequest requestDto = projectMock.updateRequestMock();
            String content = objectMapper.writeValueAsString(requestDto);

            given(projectSecurityEvaluator.isOwner(anyLong(), anyLong())).willReturn(true);

            given(projectService.update(anyLong(), any(UpdateProjectRequest.class)))
                    .willReturn(projectId);

            // when
            ResultActions perform = mockMvc.perform(
                    RestDocumentationRequestBuilders.put("/api/projects/{projectId}", projectId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(content));

            // then
            perform
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.body.projectId").value(projectId))
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andDo(document("프로젝트 수정 성공",
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Project")
                                                    .description("프로젝트 전체 정보 수정 API (OWNER 권한 필수)")
                                                    .pathParameters(
                                                            parameterWithName("projectId").description("수정할 프로젝트의 고유 식별 ID")
                                                    )
                                                    .requestSchema(Schema.schema("UpdateProjectRequest"))
                                                    .requestFields(
                                                            fieldWithPath("title").type(JsonFieldType.STRING)
                                                                    .description("변경할 프로젝트 제목 (공백 불가)"),
                                                            fieldWithPath("description").type(JsonFieldType.STRING)
                                                                    .description("변경할 프로젝트 간략 설명 (500자 이내)")
                                                                    .optional(),
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description(
                                                                            "변경할 프로젝트 진행 상태 (ACTIVE, COMPLETED, ARCHIVED)"),
                                                            fieldWithPath("startDate").type(JsonFieldType.STRING)
                                                                    .description("변경할 프로젝트 시작일 (YYYY-MM-DD)"),
                                                            fieldWithPath("endDate").type(JsonFieldType.STRING)
                                                                    .description("변경할 프로젝트 종료일 (YYYY-MM-DD)")
                                                    )
                                                    .responseSchema(Schema.schema("UpdateProjectResponse"))
                                                    .responseFields(
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("응답 상태"),
                                                            fieldWithPath("body.projectId").type(JsonFieldType.NUMBER)
                                                                    .description("수정 완료된 프로젝트 고유 ID"),
                                                            fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                                    .description("응답 시간")
                                                    )
                                                    .build()
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("프로젝트 삭제 테스트")
    class Delete {

        @Test
        @DisplayName("성공: 프로젝트 OWNER 권한 검증 통과 후 성공적으로 프로젝트를 삭제(소프트 딜리트)한다")
        @MockCustomUser
        void success() throws Exception {
            // given
            Long projectId = 1L;

            given(projectSecurityEvaluator.isOwner(anyLong(), anyLong())).willReturn(true);

            // when
            ResultActions perform = mockMvc.perform(
                    RestDocumentationRequestBuilders.delete("/api/projects/{projectId}", projectId)
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform
                    .andExpect(status().isOk()) // 💡 컨트롤러 반환 스펙인 200 OK 검증
                    .andDo(document("프로젝트 삭제 성공",
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Project")
                                                    .description("프로젝트 삭제 API (OWNER 권한 필수, 논리 삭제로 처리)")
                                                    .pathParameters(
                                                            parameterWithName("projectId").description("삭제할 프로젝트의 고유 식별 ID")
                                                    )
                                                    .build()
                                    )
                            )
                    );
        }
    }
}