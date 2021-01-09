package moe.ofs.backend.dataservice.aspect;

import moe.ofs.backend.connector.LavaSystemStatus;
import moe.ofs.backend.domain.connector.OperationPhase;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
//@Component
public class LuaInteractProxyAspect {

    @Around("@annotation(moe.ofs.backend.connector.lua.LuaInteract)")
    public Object skipMethodsIfInvalidPhase(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("joinPoint.toLongString() = " + joinPoint.toLongString());
        if (LavaSystemStatus.getPhase() == OperationPhase.RUNNING) {
            return joinPoint.proceed(joinPoint.getArgs());
        }

        return joinPoint;
    }
}
