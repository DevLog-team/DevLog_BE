package com.project.devlog.domain.tag.controller;

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
import com.project.devlog.domain.tag.dto.request.CreateTagRequest;
import com.project.devlog.domain.tag.entity.Tag;
import com.project.devlog.domain.tag.mock.TagMock;
import com.project.devlog.domain.tag.service.TagService;
import com.project.devlog.global.config.AuthTestConfig;
import com.project.devlog.global.config.SecurityConfig;
import com.project.devlog.global.security.annotation.MockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


@WebMvcTest(TagController.class)
@Import({AuthTestConfig.class, SecurityConfig.class})
@AutoConfigureRestDocs
class TagControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TagMock tagMock;

    @Autowired
    TagService tagService;

    @Nested
    @DisplayName("태그 생성 테스트")
    class create {
        @Test
        @DisplayName("성공: Tag 생성 후 tagId 반환")
        @MockCustomUser
        void success() throws Exception {
            // given
            CreateTagRequest requestDto = tagMock.createTagRequest();
            Tag tag = tagMock.domainMock();
            String content = objectMapper.writeValueAsString(requestDto);

            given(tagService.create(any(CreateTagRequest.class))).willReturn(tag.getId());

            // when
            ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/tag")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(content));

            // then
            perform
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.body.tagId").isNumber())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andDo(document("태그 생성 성공",
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Tag")
                                                    .description("태그 생성 API")
                                                    .requestSchema(Schema.schema("CreateTagRequest"))
                                                    .requestFields(
                                                            fieldWithPath("name").type(JsonFieldType.STRING)
                                                                    .description("태그 이름"),
                                                            fieldWithPath("color").type(JsonFieldType.STRING)
                                                                    .description("태그 색")
                                                    )
                                                    .responseSchema(Schema.schema("CreateTagResponse"))
                                                    .responseFields(
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("응답 상태"),
                                                            fieldWithPath("body.tagId").type(JsonFieldType.NUMBER)
                                                                    .description("태그 ID"),
                                                            fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                                    .description("응답 시간"))
                                                    .build()
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("태그 단건 조회")
    class getOne {
        @Test
        @DisplayName("성공: 태그 조회 후 반환")
        @MockCustomUser
        void success() throws Exception {
            // given
            Tag tag = tagMock.domainMock();

            given(tagService.getOne(any())).willReturn(tag);

            // when
            ResultActions perform = mockMvc.perform(
                    RestDocumentationRequestBuilders.get("/api/tags/{tagId}", tag.getId())
                            .accept(MediaType.APPLICATION_JSON));

            // then
            perform
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").isString())
                    .andExpect(jsonPath("$.body.tagId").isNumber())
                    .andExpect(jsonPath("$.body.name").isString())
                    .andExpect(jsonPath("$.body.color").isString())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andDo(document("태그 단건 조회 성공",
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Tag")
                                                    .description("태그 단건 조회 API")
                                                    .pathParameters(
                                                            parameterWithName("tagId").description("조회할 태그의 고유 식별 ID")
                                                    )
                                                    .responseSchema(Schema.schema("TagResponse"))
                                                    .responseFields(
                                                            fieldWithPath("status").type(JsonFieldType.STRING)
                                                                    .description("응답 상태"),
                                                            fieldWithPath("body.tagId").type(JsonFieldType.NUMBER)
                                                                    .description("태그 ID"),
                                                            fieldWithPath("body.name").type(JsonFieldType.STRING)
                                                                    .description("태그 이름"),
                                                            fieldWithPath("body.color").type(JsonFieldType.STRING)
                                                                    .description("태그 색"),
                                                            fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                                    .description("응답 시간"))
                                                    .build()
                                    )
                            )
                    );
        }
    }
}