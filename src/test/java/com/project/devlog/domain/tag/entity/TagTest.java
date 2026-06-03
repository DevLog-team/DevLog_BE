package com.project.devlog.domain.tag.entity;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(tag.getName()).isEqualTo("프론트엔드");
        assertThat(tag.getColor()).isEqualTo("red");
    }

    @Test
    @DisplayName("태그 삭세 성공")
    void delete_success() throws Exception {
        // given
        Tag tag = Tag.builder()
                .name("백엔드")
                .color("blue")
                .build();

        // when
        tag.delete();

        // then
        assertThat(tag.isDeleted()).isTrue();
    }
}