package com.project.devlog.domain.task.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.devlog.domain.task.dto.request.CreateTaskRequest;
import com.project.devlog.domain.task.dto.request.TaskSearchCondition;
import com.project.devlog.domain.task.dto.response.KanbanBoardResponse;
import com.project.devlog.domain.task.entity.Task;
import com.project.devlog.domain.task.entity.enums.TaskPriority;
import com.project.devlog.domain.task.entity.enums.TaskStatus;
import com.project.devlog.domain.task.mapper.TaskMapper;
import com.project.devlog.domain.task.mock.TaskMock;
import com.project.devlog.domain.task.service.TaskService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Nested
    @DisplayName("KanbanBoard 작업 목록 조회")
    class KanbanBoard {

        @Test
        @DisplayName("성공: 작업을 상태값 별로 그룹화하여 반환")
        @MockCustomUser
        void success() throws Exception {
            // given
            Long projectId = 1L;
            KanbanBoardResponse kanbanBoardResponse = taskMock.kanbanBoardResponseMock();

            given(taskService.getKanbanBoard(any())).willReturn(kanbanBoardResponse);

            // when
            ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/tasks/kanban")
                    .param("projectId", String.valueOf(projectId))
                    .accept(MediaType.APPLICATION_JSON));

            // then
            perform
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.body.board").isMap())
                    .andExpect(jsonPath("$.body.board.TODO").isArray())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andDo(document("칸반보드 작업 목록 조회 성공",
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Task")
                                                    .description("프로젝트별 칸반보드 작업 목록 조회 API")
                                                    .queryParameters(
                                                            parameterWithName("projectId").description("조회할 프로젝트 고유 식별 ID"))
                                                    .responseSchema(Schema.schema("KanbanBoardResponse"))
                                                    .responseFields(
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("응답 상태 코드/메시지"),

                                                            fieldWithPath("body.board").type(JsonFieldType.OBJECT)
                                                                    .description("상태별 칸반보드 데이터 Map"),
                                                            fieldWithPath("body.board.TODO").type(JsonFieldType.ARRAY)
                                                                    .description("할 일(TODO) 작업 목록"),
                                                            fieldWithPath("body.board.IN_PROGRESS").type(JsonFieldType.ARRAY)
                                                                    .description("진행 중(IN_PROGRESS) 작업 목록"),
                                                            fieldWithPath("body.board.DONE").type(JsonFieldType.ARRAY)
                                                                    .description("완료(DONE) 작업 목록"),
                                                            fieldWithPath("body.board.HOLD").type(JsonFieldType.ARRAY)
                                                                    .description("보류(HOLD) 작업 목록"),

                                                            fieldWithPath("body.board.*[].id").type(JsonFieldType.NUMBER)
                                                                    .description("작업 고유 ID"),
                                                            fieldWithPath("body.board.*[].title").type(JsonFieldType.STRING)
                                                                    .description("작업 제목"),
                                                            fieldWithPath("body.board.*[].description").type(
                                                                            JsonFieldType.STRING)
                                                                    .optional().description("작업 상세 설명"),
                                                            fieldWithPath("body.board.*[].status").type(JsonFieldType.STRING)
                                                                    .description("작업 현재 상태"),
                                                            fieldWithPath("body.board.*[].priority").type(JsonFieldType.STRING)
                                                                    .description("우선순위 (HIGH, MEDIUM, LOW)"),
                                                            fieldWithPath("body.board.*[].dueDate").type(JsonFieldType.STRING)
                                                                    .description("마감일 (YYYY-MM-DD)"),
                                                            fieldWithPath("body.board.*[].assigneeName").type(
                                                                            JsonFieldType.STRING)
                                                                    .optional().description("담당자 이름"),
                                                            fieldWithPath("body.board.*[].tags").type(JsonFieldType.ARRAY)
                                                                    .description("작업 태그 목록"),
                                                            fieldWithPath("body.board.*[].tags[].id").type(JsonFieldType.NUMBER)
                                                                    .description("태그 고유 ID"),
                                                            fieldWithPath("body.board.*[].tags[].name").type(
                                                                            JsonFieldType.STRING)
                                                                    .description("태그 이름"),
                                                            fieldWithPath("body.board.*[].tags[].color").type(
                                                                            JsonFieldType.STRING)
                                                                    .description("태그 색상 헥사 코드"),

                                                            fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                                    .description("응답 발행 일시")
                                                    )
                                                    .build()
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("작업 목록 조회")
    class GetList {

        @Test
        @DisplayName("성공: 조건별 검색 및 페이지네이션이 적용된 작업 목록 반환")
        @MockCustomUser
        void success() throws Exception {
            // given
            Long projectId = 1L;

            Pageable pageable = PageRequest.of(0, 10, Sort.by("dueDate").ascending());

            // Service가 반환할 Page<Task> 모킹
            Page<Task> mockTaskPage = taskMock.taskPageMock(pageable);
            given(taskService.getList(any(Long.class), any(TaskSearchCondition.class), any(Pageable.class)))
                    .willReturn(mockTaskPage);

            // when
            ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/tasks")
                    .param("projectId", String.valueOf(projectId))
                    .param("title", "API")
                    .param("assigneeId", "1")
                    .param("status", "TODO")
                    .param("priority", "HIGH")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sort", "dueDate,asc")
                    .accept(MediaType.APPLICATION_JSON));

            // then
            perform
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.body.content").isArray())
                    .andExpect(jsonPath("$.body.pageInfo.currentPage").value(1))
                    .andExpect(jsonPath("$.body.pageInfo.pageSize").value(10))
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andDo(document("작업 목록 조회 성공",
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Task")
                                                    .description("검색 조건 및 페이지네이션을 포함한 작업 목록 조회 API")
                                                    .queryParameters(
                                                            parameterWithName("projectId").description("조회할 프로젝트 고유 식별 ID"),
                                                            parameterWithName("title").optional().description("검색할 작업 제목 키워드"),
                                                            parameterWithName("assigneeId").optional()
                                                                    .description("담당자 고유 ID (전체 조회시 0 또는 NULL)"),
                                                            parameterWithName("status").optional().description(
                                                                    "작업 상태 필터 (ALL, TODO, IN_PROGRESS, DONE, HOLD)"),
                                                            parameterWithName("priority").optional()
                                                                    .description("우선순위 필터 (ALL, LOW, MEDIUM, HIGH, URGENT)"),
                                                            parameterWithName("page").optional()
                                                                    .description("조회할 페이지 번호 (0부터 시작, 기본값: 0)"),
                                                            parameterWithName("size").optional()
                                                                    .description("한 페이지당 보여줄 데이터 개수 (기본값: 10)"),
                                                            parameterWithName("sort").optional()
                                                                    .description("정렬 기준 필드 및 방향 (기본값: dueDate,asc)")
                                                    )
                                                    .responseSchema(Schema.schema("TaskListResponse"))
                                                    .responseFields(
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("응답 상태 코드/메시지"),

                                                            fieldWithPath("body").type(JsonFieldType.OBJECT)
                                                                    .description("응답 본문"),
                                                            fieldWithPath("body.content").type(JsonFieldType.ARRAY)
                                                                    .description("조회된 작업 목록 배열"),
                                                            fieldWithPath("body.content[].taskId").type(JsonFieldType.NUMBER)
                                                                    .description("작업 고유 ID"),
                                                            fieldWithPath("body.content[].title").type(JsonFieldType.STRING)
                                                                    .description("작업 제목"),
                                                            fieldWithPath("body.content[].assigneeName").type(
                                                                            JsonFieldType.STRING)
                                                                    .optional().description("담당자 이름"),
                                                            fieldWithPath("body.content[].status").type(JsonFieldType.STRING)
                                                                    .description("작업 상태"),
                                                            fieldWithPath("body.content[].priority").type(JsonFieldType.STRING)
                                                                    .description("작업 우선순위"),
                                                            fieldWithPath("body.content[].dueDate").type(JsonFieldType.STRING)
                                                                    .description("마감일 (YYYY-MM-DD)"),

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
                                                                    .description("응답 발행 일시")
                                                    )
                                                    .build()
                                    )
                            )
                    );
        }
    }
}