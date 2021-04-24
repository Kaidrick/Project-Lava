package moe.ofs.backend.aspects;

import moe.ofs.backend.annotations.ListenLavaEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.jms.TextMessage;

@Aspect
@Component
public class ListenLavaEventSelectAspect {

    @Around(value = "execution(public * *(..)) && @annotation(annotation)", argNames = "pjp, annotation")
    public Object filterEventType(ProceedingJoinPoint pjp, ListenLavaEvent annotation) throws Throwable {
        assert pjp.getArgs().length > 0;
        if (pjp.getArgs()[0] instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) pjp.getArgs()[0];
            if (annotation.value().name().equals(textMessage.getStringProperty("type"))) {
                return pjp.proceed(pjp.getArgs());
            }
        }

        return pjp;
    }
}
