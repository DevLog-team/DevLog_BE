package com.project.devlog.domain.task.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.devlog.domain.task.dto.request.CreateTaskRequest;
import com.project.devlog.domain.task.dto.response.TaskIdResponse;
import com.project.devlog.domain.task.entity.enums.TaskPriority;
import com.project.devlog.domain.task.entity.enums.TaskStatus;
import com.project.devlog.domain.task.mock.TaskMock;
import com.project.devlog.domain.task.service.TaskService;
import com.project.devlog.domain.task.mapper.TaskMapper;
import com.project.devlog.global.config.AuthTestConfig;
import com.project.devlog.global.config.SecurityConfig;
import com.project.devlog.global.security.annotation.MockCustomUser;
import java.util.List;
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

@WebMvcTest(TaskController.class)
@Import({AuthTestConfig.class, SecurityConfig.class})
@AutoConfigureRestDocs
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskMock taskMock;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskMapper taskMapper;

    @Nested
    @DisplayName("작업 생성 테스트")
    class Create {

        @Test
        @DisplayName("성공: Task 생성 후 taskId 반환")
        @MockCustomUser
        void success() throws Exception {
            // given
            Long taskId = 1L;
            CreateTaskRequest requestDto = taskMock.createTaskRequestMock();
            String content = objectMapper.writeValueAsString(requestDto);

            given(taskService.create(any(CreateTaskRequest.class))).willReturn(taskId);

            // when
            ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/task")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(content));

            // then
            perform
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.body.taskId").isNumber())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andDo(document("작업 생성 성공",
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Task")
                                                    .description("작업(Task) 생성 API")
                                                    .requestSchema(Schema.schema("CreateTaskRequest"))
                                                    .requestFields(
                                                            fieldWithPath("projectId").type(JsonFieldType.NUMBER)
                                                                    .description("프로젝트 고유 식별 ID"),
                                                            fieldWithPath("title").type(JsonFieldType.STRING)
                                                                    .description("작업 제목"),
                                                            fieldWithPath("description").type(JsonFieldType.STRING)
                                                                    .optional()
                                                                    .description("작업 상세 설명"),
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("작업 상태 (TODO, IN_PROGRESS, DONE 등)"),
                                                            fieldWithPath("priority").type(JsonFieldType.STRING)
                                                                    .description("우선순위 (HIGH, MEDIUM, LOW 등)"),
                                                            fieldWithPath("dueDate").type(JsonFieldType.STRING)
                                                                    .description("마감일 (YYYY-MM-DD)"),
                                                            fieldWithPath("tagIds").type(JsonFieldType.ARRAY)
                                                                    .optional()
                                                                    .description("연결할 태그 식별 ID 리스트"),
                                                            fieldWithPath("assigneeId").type(JsonFieldType.NUMBER)
                                                                    .description("담당 사용자 식별 ID")
                                                    )
                                                    .responseSchema(Schema.schema("TaskIdResponse"))
                                                    .responseFields(
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("응답 상태 코드/메시지"),
                                                            fieldWithPath("body.taskId").type(JsonFieldType.NUMBER)
                                                                    .description("생성된 작업 고유 ID"),
                                                            fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                                    .description("응답 발행 일시"))
                                                    .build()
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("작업 상태 목록 조회")
    class Status {

        @Test
        @DisplayName("성공: 작업 상태 목록 조회")
        @MockCustomUser
        void success() throws Exception {
            // given
            given(taskService.getStatusList()).willReturn(List.of(TaskStatus.values()));

            // when
            ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/tasks/status")
                    .accept(MediaType.APPLICATION_JSON));

            // then
            perform
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.body.taskStatusList").isArray())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andDo(document("작업 상태 목록 조회 성공",
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Task")
                                                    .description("작업 상태 목록 조회  API")
                                                    .responseSchema(Schema.schema("TaskStatusListResponse"))
                                                    .responseFields(
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("응답 상태 코드/메시지"),
                                                            fieldWithPath("body.taskStatusList").type(JsonFieldType.ARRAY)
                                                                    .description("작업 상태 목록"),
                                                            fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                                    .description("응답 발행 일시"))
                                                    .build()
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("작업 우선순위 목록 조회")
    class Priority {

        @Test
        @DisplayName("성공: 작업 우선순위 목록 조회")
        @MockCustomUser
        void success() throws Exception {
            // given
            given(taskService.getPriorityList()).willReturn(List.of(TaskPriority.values()));

            // when
            ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/tasks/priority")
                    .accept(MediaType.APPLICATION_JSON));

            // then
            perform
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.body.taskPriorities").isArray())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andDo(document("작업 우선순위 목록 조회 성공",
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Task")
                                                    .description("작업 우선순위 목록 조회  API")
                                                    .responseSchema(Schema.schema("TaskPriorityListResponse"))
                                                    .responseFields(
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("응답 상태 코드/메시지"),
                                                            fieldWithPath("body.taskPriorities").type(JsonFieldType.ARRAY)
                                                                    .description("작업 우선순위 목록"),
                                                            fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                                    .description("응답 발행 일시"))
                                                    .build()
                                    )
                            )
                    );
        }
    }
}