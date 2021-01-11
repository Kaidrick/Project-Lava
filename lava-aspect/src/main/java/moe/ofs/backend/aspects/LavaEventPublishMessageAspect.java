package moe.ofs.backend.aspects;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.events.LavaEvent;
import moe.ofs.backend.jms.Sender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Aspect
@Slf4j
public class LavaEventPublishMessageAspect {
    @Autowired
    private Sender sender;

    @Pointcut("target(moe.ofs.backend.simevent.services.SimEventService) && execution(public * *..addHandler(..))")
    public void addEventHandler() {}

    @Pointcut("target(moe.ofs.backend.simevent.services.SimEventService) && execution(public * *..removeHandler(..))")
    public void removeEventHandler() {}

    @Pointcut("execution(public * invokeHandlers(..)) && within(moe.ofs.backend.simevent..*)")
    public void invokeEventHandler() {}

    @After("addEventHandler()")
    public void logAddEventHandler(JoinPoint joinPoint) {
        assert joinPoint.getArgs().length > 0;
        Object[] args = joinPoint.getArgs();

        log.info("Added LavaEvent handler: {} {}", args[0], args[1]);
    }

    @After("removeEventHandler()")
    public void logRemoveEventHandler(JoinPoint joinPoint) {
        assert joinPoint.getArgs().length > 0;
        Object[] args = joinPoint.getArgs();

        log.info("Removed LavaEvent handler: {}", args[0]);
    }

    @After("invokeEventHandler()")
    public void publishEventMessage(JoinPoint joinPoint) {
        assert joinPoint.getArgs().length > 0;

        Object event = joinPoint.getArgs()[0];

        if (event instanceof LavaEvent) {
            sender.sendToTopicAsJson("lava.event", event, null);
        }
    }
}
