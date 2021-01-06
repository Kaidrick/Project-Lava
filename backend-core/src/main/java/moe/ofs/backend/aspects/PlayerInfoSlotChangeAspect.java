package moe.ofs.backend.aspects;

import moe.ofs.backend.discipline.aspects.NetAction;
import moe.ofs.backend.discipline.aspects.PlayerNetActionVo;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.jms.Sender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Configurable
@Aspect
public class PlayerInfoSlotChangeAspect {
    @Autowired
    private Sender sender;

    @Pointcut("execution(* moe.ofs.backend.services.*.Player*.detectSlotChange(..))")
    public void playerSlotChange() {}

    @AfterReturning(value = "playerSlotChange()", returning = "change")
    public void logPlayerSlotChange(JoinPoint joinPoint, boolean change) {
        if (change) {
            System.out.println("cut player changed slot");

            PlayerInfo previousPlayerInfo = (PlayerInfo) joinPoint.getArgs()[0];
            PlayerInfo currentPlayerInfo = (PlayerInfo) joinPoint.getArgs()[1];
            PlayerNetActionVo<PlayerInfo[]> playerNetActionVo = new PlayerNetActionVo<>();
            playerNetActionVo.setAction(NetAction.CHANGE_SLOT);
            playerNetActionVo.setObject(new PlayerInfo[] {previousPlayerInfo, currentPlayerInfo});
            playerNetActionVo.setTimestamp(System.currentTimeMillis());
            playerNetActionVo.setSuccess(true);

            sender.sendToTopicAsJson("lava.player.connection", playerNetActionVo,
                    NetAction.CHANGE_SLOT.getActionName());
        }
    }
}
