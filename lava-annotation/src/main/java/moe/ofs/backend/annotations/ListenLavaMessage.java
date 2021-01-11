package moe.ofs.backend.annotations;

import org.springframework.core.annotation.AliasFor;
import org.springframework.jms.annotation.JmsListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@JmsListener(destination = "lava.common-bus")
public @interface ListenLavaMessage {
    @AliasFor(
            annotation = JmsListener.class
    )
    String destination() default "lava.common-bus";

    @AliasFor(
            annotation = JmsListener.class
    )
    String containerFactory() default "jmsListenerContainerFactory";

    @AliasFor(
            annotation = JmsListener.class
    )
    String selector() default "";
}
