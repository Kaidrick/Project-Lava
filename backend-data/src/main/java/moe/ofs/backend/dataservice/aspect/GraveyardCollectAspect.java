package moe.ofs.backend.dataservice.aspect;

import moe.ofs.backend.dataservice.graveyard.GraveyardService;
import moe.ofs.backend.domain.dcs.poll.ExportObject;
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

    @Pointcut("execution(public void moe.ofs.backend.dataservice.exportobject.ExportObjectMapService.remove(moe.ofs.backend.domain.dcs.poll.ExportObject))")
    public void exportObjectDataRemove() {}

    @After("exportObjectDataRemove()")
    public void collectToGraveyardAfterDespawn(JoinPoint joinPoint) {
        System.out.println("export object moved to graveyard");
        Object object = joinPoint.getArgs()[0];
        if (object instanceof ExportObject) {
            service.collect((ExportObject) object);
        }
    }
}
