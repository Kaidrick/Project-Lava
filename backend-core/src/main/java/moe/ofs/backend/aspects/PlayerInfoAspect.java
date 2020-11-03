//package moe.ofs.backend.aspects;
//
//import moe.ofs.backend.domain.PlayerInfo;
//import moe.ofs.backend.jms.Sender;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//@Component
//@Aspect
//public class PlayerInfoAspect {
//    private final Sender sender;
//
//    public PlayerInfoAspect(Sender sender) {
//        this.sender = sender;
//    }
//
////    @Pointcut("within(moe.ofs.backend.services.jpa.PlayerInfoJpaService)")
//    @Pointcut("execution(public void moe.ofs.backend.services.jpa.PlayerInfoJpaService.add(..))")
//    public void logNewPlayerInfo() {}
//
//    @Pointcut("execution(public void moe.ofs.backend.services.jpa.PlayerInfoJpaService.remove(..))")
//    public void logObsoletePlayerInfo() {}
//
//    @After("logNewPlayerInfo()")
//    public void logPlayerInfoConnection(JoinPoint joinPoint) {
//        Object object = joinPoint.getArgs()[0];
//        if(object instanceof PlayerInfo) {
//            sender.sendToTopic("player.connection", (PlayerInfo) object, "connect");
//        }
//    }
//
//    @After("logObsoletePlayerInfo()")
//    public void logPlayerInfoDisconnect(JoinPoint joinPoint) {
//        Object object = joinPoint.getArgs()[0];
//        if(object instanceof PlayerInfo) {
//            sender.sendToTopic("player.connection", (PlayerInfo) object, "disconnect");
//        }
//    }
//}
