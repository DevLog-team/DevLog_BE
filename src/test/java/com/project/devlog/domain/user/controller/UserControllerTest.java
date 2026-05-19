package com.project.devlog.domain.user.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.devlog.domain.user.dto.request.SignupRequest;
import com.project.devlog.domain.user.mock.UserMock;
import com.project.devlog.domain.user.service.UserService;
import com.project.devlog.global.config.AuthTestConfig;
import com.project.devlog.global.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


@WebMvcTest(UserController.class)
@Import({AuthTestConfig.class, SecurityConfig.class})
@AutoConfigureRestDocs
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMock userMock;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 가입 테스트")
    void signup_test() throws Exception {
        // given
        SignupRequest requestDto = userMock.signupMock();
        String content = objectMapper.writeValueAsString(requestDto);

        given(userService.signup(any(SignupRequest.class))).willReturn(userMock.domainMock().getId());

        // when
        ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content));

        // then
        perform
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.body.userId").isNumber())
                .andExpect(jsonPath("$.status").isString())
                .andExpect(jsonPath("$.timestamp").isString())
                .andDo(document("user/signup",
                                resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("User")
                                                .description("회원가입 API")
                                                .requestFields(
                                                        fieldWithPath("email").type(JsonFieldType.STRING)
                                                                .description("사용자 이메일")
                                                                .attributes(key("constraint").value("이메일 형식이어야 합니다.")),
                                                        fieldWithPath("password").type(JsonFieldType.STRING)
                                                                .description("사용자 비밀번호")
                                                                .attributes(key("constraint").value("영문 + 숫자 포함 8자 이상")),
                                                        fieldWithPath("name").type(JsonFieldType.STRING)
                                                                .description("사용자 이름")
                                                                .attributes(key("constraint").value("사용자 이름")))
                                                .responseFields(
                                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                                .description("응답 상태"),
                                                        fieldWithPath("body.userId").type(JsonFieldType.NUMBER)
                                                                .description("생성된 사용자 ID"),
                                                        fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                                .description("응답 시간"))
                                                .responseHeaders(
                                                        headerWithName(HttpHeaders.LOCATION)
                                                                .description("리소스 위치"))
                                                .build()
                                )
                        )
                );
    }

}