package moe.ofs.backend.aspects;

import moe.ofs.backend.BackgroundTask;
import moe.ofs.backend.jms.Sender;
import moe.ofs.backend.message.OperationPhase;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Aspect
@Configurable
public class LuaInteractPremiseAspect {
    @Autowired
    private Sender sender;

    @Around("@annotation(moe.ofs.backend.util.LuaInteract)")
    public Object skipMethodsIfInvalidPhase(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("LuaInteract => " + joinPoint.toLongString());
        if (BackgroundTask.getCurrentTask().getPhase() == OperationPhase.RUNNING) {
            return joinPoint.proceed(joinPoint.getArgs());
        }

        return joinPoint;
    }

    @Pointcut("execution(* moe.ofs.backend.hookinterceptor.AbstractHookInterceptorProcessService.poll(..))")
    public void testAbstractClassIntercept() {}

    @After("testAbstractClassIntercept()")
    public void testIntercept(JoinPoint point) {
        System.out.println("point.getSignature() = " + point.getSignature());
    }
}
