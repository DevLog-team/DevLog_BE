package com.project.devlog.global.util;

import java.net.URI;
import org.springframework.web.util.UriComponentsBuilder;

public class UrlCreator {

    public static URI createUri (String defaultUrl, Long resourceId) {
        return UriComponentsBuilder.newInstance()
                .path(defaultUrl + "/{resource-id}")
                .buildAndExpand(resourceId)
                .toUri();
    }
}
