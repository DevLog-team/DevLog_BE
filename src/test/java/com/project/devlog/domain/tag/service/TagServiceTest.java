package com.project.devlog.domain.tag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.project.devlog.domain.tag.dto.request.CreateTagRequest;
import com.project.devlog.domain.tag.entity.Tag;
import com.project.devlog.domain.tag.mapper.TagMapper;
import com.project.devlog.domain.tag.mock.TagMock;
import com.project.devlog.domain.tag.repository.TagRepository;
import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.errorcode.TagErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @InjectMocks
    private TagService sut;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    private TagMock tagMock = new TagMock();

    @Nested
    @DisplayName("태그 생성 (create)")
    class CreateTag {

        @Test
        @DisplayName("성공: 중복되지 않은 이름의 태그는 정상적으로 저장된다.")
        void success() {
            // given
            CreateTagRequest request = tagMock.createTagRequest();

            Tag mockTag = tagMock.domainMock();

            given(tagRepository.existsTagByNameAndIsDeletedFalse(request.name())).willReturn(false);
            given(tagMapper.toTag(request)).willReturn(mockTag);
            given(tagRepository.save(any(Tag.class))).willReturn(mockTag);

            // when
            Long savedTagId = sut.create(request);

            // then
            assertThat(savedTagId).isEqualTo(1L);
            verify(tagRepository, times(1)).save(any(Tag.class));
        }

        @Test
        @DisplayName("실패: 이미 존재하는 태그 이름이면 BusinessException이 발생한다.")
        void fail_duplicateName() {
            // given
            CreateTagRequest request = tagMock.createTagRequest();

            given(tagRepository.existsTagByNameAndIsDeletedFalse(request.name())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.create(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(TagErrorCode.DUPLICATE_TAG_NAME.getMessage());

            verify(tagRepository, never()).save(any(Tag.class));
        }
    }
}