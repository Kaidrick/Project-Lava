//package moe.ofs.backend.aspects;
//
//import moe.ofs.backend.domain.PlayerInfo;
//import moe.ofs.backend.jms.Sender;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//@Component
//@Aspect
//public class PlayerInfoSlotChangeAspect {
//    private final Sender sender;
//
//    public PlayerInfoSlotChangeAspect(Sender sender) {
//        this.sender = sender;
//    }
//
//    @Pointcut("execution(public void moe.ofs.backend.services.jpa.PlayerInfoJpaService.detectSlotChange(..))")
//    public void playerSlotChange() {}
//
//    @AfterReturning(value = "playerSlotChange()", returning = "change")
//    public void logPlayerSlotChange(JoinPoint joinPoint, boolean change) {
//
//        if (change) {
//            PlayerInfo previousPlayerInfo = (PlayerInfo) joinPoint.getArgs()[0];
//            PlayerInfo currentPlayerInfo = (PlayerInfo) joinPoint.getArgs()[1];
//
//            sender.sendToTopic("player.connection", new PlayerInfo[] {previousPlayerInfo, currentPlayerInfo},
//                    "slotchange");
//        }
//    }
//}
