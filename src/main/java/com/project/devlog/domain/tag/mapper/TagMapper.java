package com.project.devlog.domain.tag.mapper;

import com.project.devlog.domain.tag.dto.request.CreateTagRequest;
import com.project.devlog.domain.tag.dto.response.TagIdResponse;
import com.project.devlog.domain.tag.entity.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {
    public TagIdResponse toIdDTO(Long tagId) {
        return new TagIdResponse(tagId);
    }

    public Tag toTag(CreateTagRequest request) {
        return Tag.builder()
                .name(request.name())
                .color(request.color())
                .build();
    }
}
