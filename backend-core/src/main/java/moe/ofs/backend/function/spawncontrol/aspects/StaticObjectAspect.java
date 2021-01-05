package moe.ofs.backend.function.spawncontrol.aspects;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.jms.Sender;
import moe.ofs.backend.object.StaticObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Aspect
@Slf4j
public class StaticObjectAspect {
    private final Sender sender;

    public StaticObjectAspect(Sender sender) {
        this.sender = sender;
    }

    @Pointcut("execution(public java.util.concurrent.CompletableFuture<moe.ofs.backend.object.StaticObject> " +
            "moe.ofs.backend.function.spawncontrol.services.*." +
            "addStaticObject(..))")
    public void addStaticObject() {}

    @Pointcut("execution(public java.util.concurrent.CompletableFuture<moe.ofs.backend.object.StaticObject> " +
            "moe.ofs.backend.function.spawncontrol.services.*.*." +
            "removeStaticObject(moe.ofs.backend.object.StaticObject || int))")
    public void removeStaticObject() {}

    // FIXME
    @Around("removeStaticObject()")
    public Object removeStaticObject(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        Object arg = args[0];

        SpawnControlVo<StaticObject> viewObject = new SpawnControlVo<>();
        if (arg instanceof Integer) {
            viewObject.setObject(StaticObject.builder()
                    .id((int) arg)
                    .build());
        } else {
            viewObject.setObject((StaticObject) arg);
        }

        viewObject.setTimestamp(System.currentTimeMillis());
        viewObject.setAction(ControlAction.REMOVE);

        boolean success = (boolean) pjp.proceed(args);
        viewObject.setSuccess(success);

        sender.sendToTopicAsJson("lava.spawn-control.static-object", viewObject, viewObject.getAction().getActionName());

        return pjp;
    }

    @AfterReturning(value = "addStaticObject()", returning = "future")
    public void staticObjectAddMessage(JoinPoint joinPoint, CompletableFuture<StaticObject> future) {
        future.thenAccept(staticObject -> {
            boolean success = staticObject.getId() != 0;
            SpawnControlVo<StaticObject> viewObject = new SpawnControlVo<>();
            viewObject.setSuccess(success);
            viewObject.setObject(staticObject);
            viewObject.setTimestamp(System.currentTimeMillis());
            viewObject.setAction(ControlAction.ADD);

            sender.sendToTopicAsJson("lava.spawn-control.static-object", viewObject, viewObject.getAction().getActionName());
        });
    }
}
