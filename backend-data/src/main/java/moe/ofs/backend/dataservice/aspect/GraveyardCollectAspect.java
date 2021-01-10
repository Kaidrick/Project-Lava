package moe.ofs.backend.dataservice.aspect;

import moe.ofs.backend.dataservice.graveyard.GraveyardService;
import moe.ofs.backend.domain.dcs.poll.ExportObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

//@Configurable
@Aspect
//@Component
public class GraveyardCollectAspect {
    private final GraveyardService service;

    public GraveyardCollectAspect(GraveyardService service) {
        this.service = service;
    }

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
