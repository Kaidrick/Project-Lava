package moe.ofs.backend.util.lua;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface LuaQueryState {
    LuaQueryEnv value() default LuaQueryEnv.MISSION_SCRIPTING;
}
