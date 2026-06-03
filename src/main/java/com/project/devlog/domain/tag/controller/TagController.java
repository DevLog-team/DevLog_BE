package com.project.devlog.domain.tag.controller;

import com.project.devlog.domain.tag.dto.request.CreateTagRequest;
import com.project.devlog.domain.tag.dto.response.TagIdResponse;
import com.project.devlog.domain.tag.mapper.TagMapper;
import com.project.devlog.domain.tag.service.TagService;
import com.project.devlog.global.util.UrlCreator;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TagController {

    private static final String DEFAULT_URL = "/api/tags";

    private final TagService tagService;
    private final TagMapper tagMapper;

    @PostMapping("/api/tag")
    public ResponseEntity<TagIdResponse> create(@Valid @RequestBody CreateTagRequest request) {
        Long tagId = tagService.create(request);
        URI location = UrlCreator.createUri(DEFAULT_URL, tagId);
        return ResponseEntity.created(location).body(tagMapper.toIdDTO(tagId));
    }



}
