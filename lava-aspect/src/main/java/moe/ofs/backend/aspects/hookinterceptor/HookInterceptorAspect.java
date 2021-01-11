package moe.ofs.backend.aspects.hookinterceptor;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class HookInterceptorAspect {

    @Autowired
    private Sender sender;

    @Pointcut("execution(private * moe.ofs.backend.hookinterceptor.AbstractHookInterceptorProcessService.processEntity(..))")
    public void hookInterceptorProcessRecordEntity() {}

    @Pointcut("execution(public * moe.ofs.backend.hookinterceptor.AbstractHookInterceptorProcessService.poll(..))")
    public void hookInterceptorTemplatePoll() {}

    @Around("hookInterceptorTemplatePoll()")
    public Object templatePollIntercept(ProceedingJoinPoint pjp) throws Throwable {
//        System.out.println("Arrays.toString(pjp.getArgs()) = " + Arrays.toString(pjp.getArgs()));
        return pjp.proceed(pjp.getArgs());
    }

    @After("hookInterceptorProcessRecordEntity()")
    public void broadcastRecordMessage(JoinPoint joinPoint) {
        Object object = joinPoint.getArgs()[0];
        sender.sendToTopicAsJson("lava.record", object, object.getClass().getTypeName());
    }
}
