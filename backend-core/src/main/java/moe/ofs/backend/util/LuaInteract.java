package moe.ofs.backend.util;

import moe.ofs.backend.message.OperationPhase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LuaInteract {
    OperationPhase value() default OperationPhase.RUNNING;
}