package com.project.devlog.domain.tag.controller;

import com.project.devlog.domain.tag.dto.request.CreateTagRequest;
import com.project.devlog.domain.tag.dto.request.UpdateTagRequest;
import com.project.devlog.domain.tag.dto.response.TagIdResponse;
import com.project.devlog.domain.tag.dto.response.TagListResponse;
import com.project.devlog.domain.tag.dto.response.TagResponse;
import com.project.devlog.domain.tag.entity.Tag;
import com.project.devlog.domain.tag.mapper.TagMapper;
import com.project.devlog.domain.tag.service.TagService;
import com.project.devlog.global.util.UrlCreator;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("/api/tags/{tagId}")
    public ResponseEntity<TagResponse> getOne(@PathVariable Long tagId) {
        Tag tag = tagService.getOne(tagId);
        return ResponseEntity.ok().body(tagMapper.toTagDto(tag));
    }

    @GetMapping("/api/tags")
    public ResponseEntity<TagListResponse> getList() {
        List<Tag> tags = tagService.getList();
        return ResponseEntity.ok().body(tagMapper.toTagListDto(tags));
    }

    @PutMapping("/api/tags/{tagId}")
    public ResponseEntity<TagIdResponse> update(
            @PathVariable Long tagId,
            @Valid @RequestBody UpdateTagRequest request
    ) {
        Long updatedTagId = tagService.update(tagId, request);
        URI location = UrlCreator.createUri(DEFAULT_URL, tagId);
        return ResponseEntity.ok()
                .header(HttpHeaders.LOCATION, location.toString())
                .body(tagMapper.toIdDTO(tagId));
    }
}
