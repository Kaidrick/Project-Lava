package moe.ofs.backend.aspects;

import moe.ofs.backend.connector.LavaSystemStatus;
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

@Aspect
@Configurable
public class LuaInteractPremiseAspect {
    @Autowired
    private Sender sender;

    @Around("@annotation(moe.ofs.backend.connector.lua.LuaInteract)")
    public Object skipMethodsIfInvalidPhase(ProceedingJoinPoint joinPoint) throws Throwable {
//        System.out.println("joinPoint.toLongString() = " + joinPoint.toLongString());
        if (LavaSystemStatus.getPhase() == OperationPhase.RUNNING) {
            return joinPoint.proceed(joinPoint.getArgs());
        }

        return joinPoint;
    }

    @Pointcut("execution(public * moe.ofs.backend.hookinterceptor.AbstractHookInterceptorProcessService.poll(..))")
    public void testAbstractClassIntercept() {}

    @After("testAbstractClassIntercept()")
    public void testIntercept(JoinPoint point) {
        System.out.println("testIntercept => point.getSignature() = " + point.getSignature());
    }
}
