package moe.ofs.backend.services.mizdb;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface InjectionEnvironment {
    Environment value() default Environment.MISSION;
}
