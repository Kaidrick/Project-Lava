package moe.ofs.backend.annotations;

import moe.ofs.backend.domain.events.EventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ListenLavaMessage(destination = "lava.event")
public @interface ListenLavaEvent {
    EventType value() default EventType.INVALID;
}
