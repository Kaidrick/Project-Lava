package moe.ofs.backend.aspects;

import moe.ofs.backend.domain.behaviors.net.NetAction;
import moe.ofs.backend.domain.behaviors.net.PlayerNetActionVo;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;
import moe.ofs.backend.jms.Sender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Aspect
public class PlayerInfoSlotChangeAspect {
    @Autowired
    private Sender sender;

    @Pointcut("execution(* moe.ofs.backend.dataservice.player.PlayerInfoMapService.detectSlotChange(..))")
    public void playerSlotChange() {}

    @AfterReturning(value = "playerSlotChange()", returning = "change")
    public void logPlayerSlotChange(JoinPoint joinPoint, boolean change) {
        if (change) {
            System.out.println("cut player changed slot");

            // FIXME: joinPoint = execution(public boolean moe.ofs.backend.dataservice.map.PlayerInfoMapService.detectSlotChange(moe.ofs.backend.domain.dcs.poll.PlayerInfo, moe.ofs.backend.domain.dcs.poll.PlayerInfo))
            // FIXME: joinPoint = execution(public final boolean moe.ofs.backend.dataservice.map.PlayerInfoMapService..EnhancerBySpringCGLIB..425270c2.detectSlotChange(moe.ofs.backend.domain.dcs.poll.PlayerInfo, moe.ofs.backend.domain.dcs.poll.PlayerInfo))
            System.out.println("joinPoint = " + joinPoint.toLongString());

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
