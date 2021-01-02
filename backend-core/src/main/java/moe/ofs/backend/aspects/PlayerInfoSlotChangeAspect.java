package moe.ofs.backend.aspects;

import moe.ofs.backend.discipline.aspects.NetAction;
import moe.ofs.backend.discipline.aspects.PlayerNetActionVo;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.jms.Sender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class PlayerInfoSlotChangeAspect {
    private final Sender sender;

    public PlayerInfoSlotChangeAspect(Sender sender) {
        this.sender = sender;
    }

    @Pointcut("execution(public boolean moe.ofs.backend.services.*." +
            "detectSlotChange(moe.ofs.backend.domain.PlayerInfo, moe.ofs.backend.domain.PlayerInfo))")
    public void playerSlotChange() {}

    @Pointcut("execution(public void moe.ofs.backend.services.map.PlayerInfoMapService.dispose(..))")
    public void dispose() {}

    @Before("dispose()")
    public void testsetset() {
        System.out.println("test test test");
    }

//    @Around("playerSlotChange()")
//    public Object testSlotChange(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        System.out.println("player slot change boolean");
//        proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
//        return proceedingJoinPoint;
//    }

//    @AfterReturning(value = "playerSlotChange()", returning = "change")
//    public void logPlayerSlotChange(JoinPoint joinPoint, boolean change) {
//        System.out.println("point cut player change slot");
//
//        if (change) {
//            PlayerInfo previousPlayerInfo = (PlayerInfo) joinPoint.getArgs()[0];
//            PlayerInfo currentPlayerInfo = (PlayerInfo) joinPoint.getArgs()[1];
//            PlayerNetActionVo<PlayerInfo[]> playerNetActionVo = new PlayerNetActionVo<>();
//            playerNetActionVo.setAction(NetAction.CHANGE_SLOT);
//            playerNetActionVo.setObject(new PlayerInfo[] {previousPlayerInfo, currentPlayerInfo});
//            playerNetActionVo.setTimestamp(System.currentTimeMillis());
//            playerNetActionVo.setSuccess(true);
//
//            sender.sendToTopic("lava.player.connection", playerNetActionVo,
//                    NetAction.CHANGE_SLOT.getActionName());
//        }
//    }
}
