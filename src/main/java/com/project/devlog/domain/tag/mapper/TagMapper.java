package com.project.devlog.domain.tag.mapper;

import com.project.devlog.domain.tag.dto.request.CreateTagRequest;
import com.project.devlog.domain.tag.dto.response.TagIdResponse;
import com.project.devlog.domain.tag.dto.response.TagListResponse;
import com.project.devlog.domain.tag.dto.response.TagResponse;
import com.project.devlog.domain.tag.entity.Tag;
import java.util.List;
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

    public TagResponse toTagDto(Tag tag) {
        return new TagResponse(
                tag.getId(),
                tag.getName(),
                tag.getColor()
        );
    }

    public TagListResponse toTagListDto(List<Tag> tags) {
        List<TagResponse> list = tags.stream().map(this::toTagDto).toList();
        return new TagListResponse(list);
    }
}
