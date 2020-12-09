package moe.ofs.backend.util.lua;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LuaQueryState {
    QueryEnv value() default QueryEnv.MISSION_SCRIPTING;
}
