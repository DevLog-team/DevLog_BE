package com.project.devlog.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private SecretKey key;
    private JwtProperties jwtProperties;

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        byte[] byteSecretKey = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        this.key = Keys.hmacShaKeyFor(byteSecretKey);
    }

    public String generateAccessToken(String subject, Long id, Collection<? extends GrantedAuthority> authorities) {
        long now = new Date().getTime();

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(subject)
                .claim("id", id)
                .claim( jwtProperties.getAuthoritiesKey(), roles)
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtProperties.getAccessExpirationTime()))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String subject, Long id) {
        long now = new Date().getTime();

        return Jwts.builder()
                .subject(subject)
                .claim("id", id)
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtProperties.getRefreshExpirationTime()))
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
