package com.project.devlog.global.security.annotation;

import com.project.devlog.domain.user.entity.enums.UserRole;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.test.context.support.WithSecurityContext;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockCustomUserSecurityContextFactory.class)
public @interface MockCustomUser {
    long userId() default 1L;

    UserRole userRole() default UserRole.USER;
}
