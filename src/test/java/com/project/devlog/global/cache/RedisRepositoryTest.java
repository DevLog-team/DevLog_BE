package com.project.devlog.global.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataRedisTest(excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
})
@Import({RedisRepository.class, ObjectMapper.class})
class RedisRepositoryTest {

    private static final String REDIS_IMAGE = "redis:7.0.8-alpine";
    private static final int REDIS_PORT = 6379;
    private static final GenericContainer redis;

    static {
        redis = new GenericContainer(REDIS_IMAGE)
                .withExposedPorts(REDIS_PORT)
                .withReuse(true);
        redis.start();
    }

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(REDIS_PORT)
                .toString());
    }

    @Autowired
    RedisRepository redisRepository;

    @Autowired
    com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    @DisplayName("redis 저장 테스트")
    void save_test() throws Exception {
        // given
        String key = "test";
        String value = "test";

        // when
        redisRepository.save(key, value, 10, TimeUnit.SECONDS);
        String result = redisRepository.findByKey(key);

        // then
        assertNotNull(result);
        assertThat(result).isEqualTo(value);
    }

    @Test
    @DisplayName("redis 삭제 테스트")
    void delete_test() throws Exception {
        // given
        String key = "test";
        String value = "test";

        // when
        redisRepository.save(key, value, 10, TimeUnit.SECONDS);
        redisRepository.delete(key);
        String result = redisRepository.findByKey(key);

        // then
        assertNull(result);
    }
}