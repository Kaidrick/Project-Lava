package moe.ofs.backend.aspects;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.BackgroundTask;
import moe.ofs.backend.message.OperationPhase;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class MethodOperationPhasePremiseAspect {

    @Around("@annotation(moe.ofs.backend.util.LuaInteract)")
    public Object skipMethodsIfInvalidPhase(ProceedingJoinPoint joinPoint) throws Throwable {
        if (BackgroundTask.getCurrentTask().getPhase() == OperationPhase.RUNNING) {
            return joinPoint.proceed(joinPoint.getArgs());
        }

        return joinPoint;
    }
}
