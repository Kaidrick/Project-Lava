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

    @Pointcut("execution(* moe.ofs.backend.services.map.*.dispose(..))")
    public void dispose() {}

    @Pointcut("execution(* moe.ofs.backend.services.*.Player*.detectSlotChange(..))")
    public void playerSlotChange() {}

    @AfterReturning(value = "playerSlotChange()", returning = "change")
    public void logPlayerSlotChange(JoinPoint joinPoint, boolean change) {
        System.out.println("point cut player change slot");

        if (change) {
            PlayerInfo previousPlayerInfo = (PlayerInfo) joinPoint.getArgs()[0];
            PlayerInfo currentPlayerInfo = (PlayerInfo) joinPoint.getArgs()[1];
            PlayerNetActionVo<PlayerInfo[]> playerNetActionVo = new PlayerNetActionVo<>();
            playerNetActionVo.setAction(NetAction.CHANGE_SLOT);
            playerNetActionVo.setObject(new PlayerInfo[] {previousPlayerInfo, currentPlayerInfo});
            playerNetActionVo.setTimestamp(System.currentTimeMillis());
            playerNetActionVo.setSuccess(true);

            sender.sendToTopic("lava.player.connection", playerNetActionVo,
                    NetAction.CHANGE_SLOT.getActionName());
        }
    }
}
