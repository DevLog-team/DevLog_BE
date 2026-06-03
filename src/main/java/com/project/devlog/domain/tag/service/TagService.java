package com.project.devlog.domain.tag.service;

import com.project.devlog.domain.tag.dto.request.CreateTagRequest;
import com.project.devlog.domain.tag.entity.Tag;
import com.project.devlog.domain.tag.mapper.TagMapper;
import com.project.devlog.domain.tag.repository.TagRepository;
import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.errorcode.TagErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Transactional
    public Long create(CreateTagRequest request) {
        validateDuplicateName(request.name());
        Tag tag = tagMapper.toTag(request);
        tagRepository.save(tag);
        return tag.getId();
    }

    private void validateDuplicateName(String name) {
        boolean isExistedName = tagRepository.existsTagByNameAndIsDeletedFalse(name);
        if (isExistedName) {
            throw new BusinessException(TagErrorCode.DUPLICATE_TAG_NAME);
        }
    }

    public Tag getOne(Long tagId) {
        return findById(tagId);
    }

    private Tag findById(Long tagId) {
        return tagRepository.findByIdAndIsDeletedFalse(tagId)
                .orElseThrow(() -> new BusinessException(TagErrorCode.TAG_NOT_FOUND));
    }
}
