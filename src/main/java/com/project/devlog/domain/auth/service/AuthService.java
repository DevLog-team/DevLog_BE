package com.project.devlog.domain.auth.service;

import com.project.devlog.domain.user.entity.User;
import com.project.devlog.domain.user.entity.enums.UserRole;
import com.project.devlog.domain.user.repository.UserRepository;
import com.project.devlog.global.cache.RedisKeyGenerator;
import com.project.devlog.global.cache.RedisRepository;
import com.project.devlog.global.exception.BusinessException;
import com.project.devlog.global.exception.errorcode.AuthErrorCode;
import com.project.devlog.global.exception.errorcode.UserErrorCode;
import com.project.devlog.global.security.jwt.JwtProperties;
import com.project.devlog.global.security.jwt.JwtProvider;
import com.project.devlog.global.security.vo.CustomUserDetails;
import com.project.devlog.global.util.CookieUtils;
import com.project.devlog.global.util.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RedisRepository redisRepository;
    private final ResponseUtil responseUtil;
    private final CookieUtils cookieUtils;
    private final JwtProperties jwtProperties;
    private final JwtProvider jwtProvider;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findUserByEmailOrThrow(email);
        return CustomUserDetails.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .roles(List.of(user.getRole().name()))
                .build();
    }

    public void registerRefreshToken(Long userId, String refreshToken) {
        String refreshKey = findRefreshKey(userId);
        redisRepository.save(refreshKey, refreshToken, jwtProperties.getRefreshExpirationTime(), TimeUnit.SECONDS);
    }

    public void deleteRefreshToken(Long userId) {
        String refreshKey = findRefreshKey(userId);
        redisRepository.delete(refreshKey);
    }

    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        try {
            String preToken = extractRefreshTokenOrThrow(request);

            User user = getUserBySubject(preToken);
            verifyRefreshToken(user.getId(), preToken);

            String newAccess = generateAccessToken(user);
            String newRefresh = generateRefreshToken(user);

            registerRefreshToken(user.getId(), newRefresh);
            responseUtil.addTokensToResponse(response, newAccess, newRefresh);
        } catch (ExpiredJwtException ee) {
            throw new BusinessException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (SignatureException e) {
            throw new BusinessException(AuthErrorCode.INVALID_SIGNATURE_ACCESS_TOKEN);
        } catch (JwtException e) {
            throw new BusinessException(AuthErrorCode.UNAUTHENTICATED);
        }

    }

    private User findUserByEmailOrThrow(String email) {
        return userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_EXIST_USER));
    }

    private String extractRefreshTokenOrThrow(HttpServletRequest request) {
        String refreshToken = cookieUtils.extractRefreshToken(request);
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }
        return refreshToken;
    }

    private User getUserBySubject(String preToken) {
        String email = findSubjectFromRefresh(preToken);
        return findUserByEmailOrThrow(email);
    }

    private String findSubjectFromRefresh(String refreshToken) {
        return jwtProvider.getClaims(refreshToken).getSubject();
    }

    private void verifyRefreshToken(Long userId, String preToken) {
        String refreshFromRedis = getRefreshFromRedis(userId);
        if (refreshFromRedis == null || !refreshFromRedis.equals(preToken)) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private String getRefreshFromRedis(Long userId) {
        String refreshTokenKey = findRefreshKey(userId);
        return redisRepository.findByKey(refreshTokenKey);
    }

    private String findRefreshKey(Long userId) {
        return RedisKeyGenerator.getRefreshTokenKey(userId);
    }

    private String generateAccessToken(User user) {
        List<GrantedAuthority> authorities = mapToAuthorities(user.getRole());
        return jwtProvider.generateAccessToken(
                user.getEmail(),
                user.getId(),
                authorities
        );
    }

    private List<GrantedAuthority> mapToAuthorities(UserRole role) {
        return List.of((GrantedAuthority) () -> "ROLE_" + role.name());
    }

    private String generateRefreshToken(User user) {
        return jwtProvider.generateRefreshToken(
                user.getEmail(),
                user.getId()
        );
    }
}
