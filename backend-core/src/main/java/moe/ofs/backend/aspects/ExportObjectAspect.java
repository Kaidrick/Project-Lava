package moe.ofs.backend.aspects;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.jms.Sender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ExportObjectAspect {

    private final Sender sender;

    public ExportObjectAspect(Sender sender) {
        this.sender = sender;
    }

    @Pointcut("execution(public void moe.ofs.backend.services.jpa.*.remove(moe.ofs.backend.domain.ExportObject))")
    public void exportObjectDataRemove() {}

    @Pointcut("execution(public void moe.ofs.backend.services.jpa.*.add(moe.ofs.backend.domain.ExportObject))")
    public void exportObjectDataAdd() {}

    @Pointcut("execution(public void moe.ofs.backend.services.jpa.ExportObjectDeltaJpaService." +
            "update(moe.ofs.backend.domain.ExportObject))")
    public void exportObjectDataUpdate() {}  // example of export object update listener

    @After("exportObjectDataAdd()")
    public void logExportUnitSpawn(JoinPoint joinPoint) {
        Object object = joinPoint.getArgs()[0];
        if(object instanceof ExportObject) {
            sender.sendToTopic("unit.spawncontrol", (ExportObject) object, "spawn");
        }
    }

    @After("exportObjectDataRemove()")
    private void logExportUnitDespawn(JoinPoint joinPoint) {
        Object object = joinPoint.getArgs()[0];
        if(object instanceof ExportObject) {
            sender.sendToTopic("unit.spawncontrol", (ExportObject) object, "despawn");
        }
    }

    @After("exportObjectDataUpdate()")
    private void logExportObjectDataUpdate() {

    }

}
