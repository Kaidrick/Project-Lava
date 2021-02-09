package moe.ofs.backend.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPermission {
    String[] roles();

    String[] groups();

    String[] nonRoles();

    String[] nonGroups();

    boolean requiredAccessToken() default false;

    String description() default "Lava自定义权限校验注解";
}
