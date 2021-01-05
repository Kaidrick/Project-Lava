package moe.ofs.backend.aspects;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.function.spawncontrol.aspects.ControlAction;
import moe.ofs.backend.jms.Sender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

//@Component
@Aspect
public class ExportObjectAspect {

    private final Sender sender;

    public ExportObjectAspect(Sender sender) {
        this.sender = sender;
    }

    @Pointcut("execution(public void " +
            "moe.ofs.backend.services.map.ExportObjectMapService.add(..))")
    public void exportObjectDataAdd() {
    }

    @Pointcut("execution(public void " +
            "moe.ofs.backend.services.map.ExportObjectMapService.remove(..))")
    public void exportObjectDataRemove() {
    }

    //    @Pointcut("execution(public void moe.ofs.backend.services.jpa.ExportObjectDeltaJpaService." +
//            "update(moe.ofs.backend.domain.ExportObject))")
    @Pointcut("execution(public moe.ofs.backend.domain.ExportObject " +
            "moe.ofs.backend.services.map.ExportObjectMapService.update(..))")
    public void exportObjectDataUpdate() {
    }  // example of export object update listener

    @After("exportObjectDataAdd()")
    public void logExportUnitSpawn(JoinPoint joinPoint) {
        Object object = joinPoint.getArgs()[0];
        if (object instanceof ExportObject) {
            sender.sendToTopicAsJson("lava.spawn-control.export-object",
                    object, ControlAction.SPAWN.getActionName());
        }
    }

    @After("exportObjectDataRemove()")
    public void logExportUnitDespawn(JoinPoint joinPoint) {
        Object object = joinPoint.getArgs()[0];
        if (object instanceof ExportObject) {
            sender.sendToTopicAsJson("lava.spawn-control.export-object",
                    object, ControlAction.DESPAWN.getActionName());
        }
    }

    @AfterReturning(value = "exportObjectDataUpdate()", returning = "update")
    public void logExportObjectDataUpdate(JoinPoint joinPoint, ExportObject update) {
//        Object object = joinPoint.getArgs()[0];
        if (update != null) {
            sender.sendToTopicAsJson("lava.spawn-control.export-object",
                    update, ControlAction.UPDATE.getActionName());
        }
    }

}
