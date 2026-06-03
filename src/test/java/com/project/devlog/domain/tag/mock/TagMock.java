package com.project.devlog.domain.tag.mock;

import com.project.devlog.domain.tag.dto.request.CreateTagRequest;
import com.project.devlog.domain.tag.entity.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMock {

    private static final Long tagId = 1L;
    private static final String name = "백엔드";
    private static final String color = "blue";

    public Tag domainMock() {
        return Tag.builder()
                .id(tagId)
                .name(name)
                .color(color)
                .build();
    }

    public CreateTagRequest createTagRequest() {
        return new CreateTagRequest(name, color);
    }

}
