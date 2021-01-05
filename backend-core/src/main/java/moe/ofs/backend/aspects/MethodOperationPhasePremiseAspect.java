package moe.ofs.backend.aspects;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.BackgroundTask;
import moe.ofs.backend.message.OperationPhase;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
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

    @Pointcut("execution(* moe.ofs.backend.hookinterceptor.AbstractHookInterceptorProcessService.poll(..))")
    public void testAbstractClassIntercept() {}

    @After("testAbstractClassIntercept()")
    public void testIntercept(JoinPoint point) {
        System.out.println("********************************************************");
        System.out.println("point = " + point);
        System.out.println("point.getSignature() = " + point.getSignature());
    }
}
