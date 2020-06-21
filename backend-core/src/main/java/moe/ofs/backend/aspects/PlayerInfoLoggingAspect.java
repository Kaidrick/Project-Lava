package moe.ofs.backend.aspects;

import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.jms.Sender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
//@Aspect
public class PlayerInfoLoggingAspect {
    private final Sender sender;

    public PlayerInfoLoggingAspect(Sender sender) {
        this.sender = sender;
    }

    @Pointcut("within(moe.ofs.backend.services.jpa.PlayerInfoJpaService))")
    public void logNewPlayerInfo() {}

    @Pointcut("execution(public void update(..))")
    public void badTest() {}

    @Pointcut("execution(public void moe.ofs.backend.services.jpa.PlayerInfoJpaService.remove(..))")
    public void logObsoletePlayerInfo() {}

    @After("logNewPlayerInfo()")
    public void logPlayerInfoConnection(JoinPoint joinPoint) {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + joinPoint.getSignature());
//        Object object = joinPoint.getArgs()[0];
//        if(object instanceof PlayerInfo) {
//            sender.sendToTopic("player.connection", (PlayerInfo) object, "connect");
//        }
    }

    @After("logObsoletePlayerInfo()")
    public void logPlayerInfoDisconnect(JoinPoint joinPoint) {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Object object = joinPoint.getArgs()[0];
        if(object instanceof PlayerInfo) {
            sender.sendToTopic("player.connection", (PlayerInfo) object, "disconnect");
        }
    }
}
