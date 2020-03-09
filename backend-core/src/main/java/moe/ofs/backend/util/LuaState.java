package moe.ofs.backend.util;

import moe.ofs.backend.request.Level;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LuaState {
    Level value();
}
