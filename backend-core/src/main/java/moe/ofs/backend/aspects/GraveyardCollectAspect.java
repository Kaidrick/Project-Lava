package moe.ofs.backend.aspects;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.services.GraveyardService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class GraveyardCollectAspect {

    private final GraveyardService service;

    public GraveyardCollectAspect(GraveyardService service) {
        this.service = service;
    }

    @Pointcut("execution(public void moe.ofs.backend.services.map.ExportObjectMapService.remove(moe.ofs.backend.domain.ExportObject))")
    public void exportObjectDataRemove() {}

    @After("exportObjectDataRemove()")
    private void collectToGraveyardAfterDespawn(JoinPoint joinPoint) {
        Object object = joinPoint.getArgs()[0];
        if (object instanceof ExportObject) {
            service.collect((ExportObject) object);
        }
    }
}
