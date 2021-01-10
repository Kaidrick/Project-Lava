package moe.ofs.backend.aspects;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.behaviors.spawnctl.ControlAction;
import moe.ofs.backend.domain.behaviors.spawnctl.SpawnControlVo;
import moe.ofs.backend.jms.Sender;
import moe.ofs.backend.object.StaticObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.concurrent.CompletableFuture;

@Configurable
@Aspect
@Slf4j
public class StaticObjectAspect {
    @Autowired
    private Sender sender;

    @Pointcut("execution(public java.util.concurrent.CompletableFuture<moe.ofs.backend.object.StaticObject> " +
            "moe.ofs.backend.function.spawncontrol.services.impl.StaticObjectServiceImpl.addStaticObject(..))")
    public void addStaticObject() {}

    @Pointcut("execution(public java.util.concurrent.CompletableFuture<moe.ofs.backend.object.StaticObject> " +
            "moe.ofs.backend.function.spawncontrol.services.impl.StaticObjectServiceImpl." +
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
