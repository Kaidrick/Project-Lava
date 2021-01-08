package moe.ofs.backend.aspects;

import moe.ofs.backend.domain.dcs.poll.ExportObject;
import moe.ofs.backend.dataservice.GraveyardService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Aspect
public class GraveyardCollectAspect {

    @Autowired
    private GraveyardService service;

    @Pointcut("execution(public void moe.ofs.backend.dataservice.map.ExportObjectMapService.remove(moe.ofs.backend.domain.dcs.poll.ExportObject))")
    public void exportObjectDataRemove() {}

    @After("exportObjectDataRemove()")
    public void collectToGraveyardAfterDespawn(JoinPoint joinPoint) {
        Object object = joinPoint.getArgs()[0];
        if (object instanceof ExportObject) {
            service.collect((ExportObject) object);
        }
    }
}
