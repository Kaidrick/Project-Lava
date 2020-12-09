package moe.ofs.backend.util.lua;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LuaQueryState {
    LuaQueryEnv value() default LuaQueryEnv.MISSION_SCRIPTING;
}
