package moe.ofs.backend.dataservice.aspect;

import moe.ofs.backend.dataservice.exportobject.ExportObjectService;
import moe.ofs.backend.dataservice.graveyard.GraveyardService;
import moe.ofs.backend.domain.dcs.poll.ExportObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Optional;

@Configurable
@Aspect
public class GraveyardCollectAspect {
    @Autowired
    private GraveyardService graveyardService;

    @Autowired
    private ExportObjectService exportObjectService;

    @Pointcut("execution(public void moe.ofs.backend.dataservice.exportobject.ExportObjectMapService.remove(moe.ofs.backend.domain.dcs.poll.ExportObject))")
    public void exportObjectDataRemove() {}

    @Around("exportObjectDataRemove()")
    public Object collectToGraveyardAfterDespawn(ProceedingJoinPoint pjp) throws Throwable {
        assert pjp.getArgs().length > 0;
        Object object = pjp.getArgs()[0];
        Optional<ExportObject> optionalBufferRecord = Optional.empty();
        if (object instanceof ExportObject) {
            optionalBufferRecord = exportObjectService.findByRuntimeId(((ExportObject) object).getRuntimeID());
        }

        Object res = pjp.proceed(pjp.getArgs());

        optionalBufferRecord.ifPresent(exportObject -> {
            graveyardService.collect(exportObject);
            System.out.println("export object moved to graveyard: " + exportObject.toString());
        });

        return res;
    }
}
