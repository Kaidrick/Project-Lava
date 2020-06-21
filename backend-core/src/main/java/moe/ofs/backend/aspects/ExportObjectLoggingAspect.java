package moe.ofs.backend.aspects;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.jms.Sender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
//@Aspect
public class ExportObjectLoggingAspect {

    private final Sender sender;

    public ExportObjectLoggingAspect(Sender sender) {
        this.sender = sender;
    }

    @Pointcut("execution(public void moe.ofs.backend.services.jpa.*.remove(moe.ofs.backend.domain.ExportObject))")
    public void logObsoleteExportData() {}

    @Pointcut("execution(public void moe.ofs.backend.services.jpa.*.add(moe.ofs.backend.domain.ExportObject))")
    public void logNewExportData() {}

    @After("logNewExportData()")
    public void logExportUnitSpawn(JoinPoint joinPoint) {
        Object object = joinPoint.getArgs()[0];
        if(object instanceof ExportObject) {
            sender.sendToTopic("unit.spawncontrol", (ExportObject) object, "spawn");
        }
    }

    @After("logObsoleteExportData()")
    private void logExportUnitDespawn(JoinPoint joinPoint) {
        Object object = joinPoint.getArgs()[0];
        if(object instanceof ExportObject) {
            sender.sendToTopic("unit.spawncontrol", (ExportObject) object, "despawn");
        }
    }

}
