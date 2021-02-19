package moe.ofs.backend.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPermission {
    String[] roles() default {};

    String[] groups() default {};

    String[] nonRoles() default {};

    String[] nonGroups() default {};

    boolean requiredAccessToken() default false;

    String description() default "Lava自定义权限校验注解";
}
