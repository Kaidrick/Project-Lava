package moe.ofs.backend.aspects;

import moe.ofs.backend.connector.LavaSystemStatus;
import moe.ofs.backend.connector.lua.LuaInteract;
import moe.ofs.backend.domain.connector.OperationPhase;
import moe.ofs.backend.jms.Sender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Arrays;

@Aspect
@Configurable
public class LuaInteractPremiseAspect {
    @Autowired
    private Sender sender;

    @Around(value = "@annotation(annotation)", argNames = "pjp, annotation")
    public Object skipMethodsIfInvalidPhase(ProceedingJoinPoint pjp, LuaInteract annotation) throws Throwable {
        if (Arrays.asList(annotation.value()).contains(LavaSystemStatus.getPhase())) {
            return pjp.proceed(pjp.getArgs());
        }

        return pjp;
    }
}
