package moe.ofs.backend.aspects;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.services.GraveyardService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Configurable
@Aspect
public class GraveyardCollectAspect {

    @Autowired
    private GraveyardService service;

    @Pointcut("execution(public void moe.ofs.backend.services.map.ExportObjectMapService.remove(moe.ofs.backend.domain.ExportObject))")
    public void exportObjectDataRemove() {}

    @After("exportObjectDataRemove()")
    public void collectToGraveyardAfterDespawn(JoinPoint joinPoint) {
        Object object = joinPoint.getArgs()[0];
        if (object instanceof ExportObject) {
            service.collect((ExportObject) object);
        }
    }
}
