package com.project.devlog.global.cache;

import static com.project.devlog.global.cache.CacheNames.*;

public class RedisKeyGenerator {

    public static String getRefreshTokenKey(Long userId) { return REFRESH_TOKEN + SEPARATOR + userId; }
}
