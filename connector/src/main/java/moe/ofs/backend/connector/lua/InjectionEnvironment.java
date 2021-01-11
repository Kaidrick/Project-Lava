package moe.ofs.backend.connector.lua;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface InjectionEnvironment {
    Environment value() default Environment.MISSION;
}
