package com.project.devlog.global.security.annotation;

import java.util.Arrays;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class MockCustomUserSecurityContextFactory implements WithSecurityContextFactory<MockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(MockCustomUser annotation) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                annotation.userId(),
                "password",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_" + annotation.userRole().name())));
        securityContext.setAuthentication(authenticationToken);
        return securityContext;
    }
}
