package moe.ofs.backend.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LavaSystemArchitecture {
    @Pointcut("within(moe.ofs.backend.connector..*)")
    public void inConnector() {}

    @Pointcut("within(moe.ofs.backend.hookinterceptor..*)")
    public void inHookInterceptorTemplate() {}
}
