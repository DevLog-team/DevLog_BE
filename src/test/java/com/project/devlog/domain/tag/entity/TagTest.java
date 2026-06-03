package com.project.devlog.domain.tag.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TagTest {

    @Test
    @DisplayName("태그 수정 성공")
    void update_success() throws Exception {
        // given
        Tag tag = Tag.builder()
                .name("백엔드")
                .color("blue")
                .build();

        // when
        tag.update("프론트엔드", "red");

        // then
        assert(tag.getName()).equals("프론트엔드");
        assert(tag.getColor()).equals("red");
    }
}