package moe.ofs.backend.domain.dcs;

import moe.ofs.backend.domain.connector.Level;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LuaState {
    Level value();
}
