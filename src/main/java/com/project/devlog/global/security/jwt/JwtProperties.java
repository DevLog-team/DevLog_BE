package com.project.devlog.global.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("jwt")
public class JwtProperties {
    private int accessExpirationTime;
    private int refreshExpirationTime;
    private String authoritiesKey;
    private String tokenPrefix;
    private String secretKey;
}
