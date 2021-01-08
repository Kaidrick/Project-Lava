package moe.ofs.backend.aspects;

import moe.ofs.backend.discipline.aspects.NetAction;
import moe.ofs.backend.discipline.aspects.PlayerNetActionVo;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;
import moe.ofs.backend.jms.Sender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Aspect
public class PlayerInfoAspect {
    @Autowired
    private Sender sender;

//    @Pointcut("within(moe.ofs.backend.services.jpa.PlayerInfoJpaService)")
    @Pointcut("execution(public void moe.ofs.backend.services.*.Player*.add(..))")
    public void logNewPlayerInfo() {}

    @Pointcut("execution(public void moe.ofs.backend.services.*.Player*.remove(..))")
    public void logObsoletePlayerInfo() {}

    @After("logNewPlayerInfo()")
    public void logPlayerInfoConnection(JoinPoint joinPoint) {
        System.out.println("point cut player new");
        Object object = joinPoint.getArgs()[0];
        if(object instanceof PlayerInfo) {
            PlayerNetActionVo<PlayerInfo> playerNetActionVo = new PlayerNetActionVo<>();
            playerNetActionVo.setAction(NetAction.CONNECT);
            playerNetActionVo.setObject((PlayerInfo) object);
            playerNetActionVo.setTimestamp(System.currentTimeMillis());
            playerNetActionVo.setSuccess(true);
            sender.sendToTopic("lava.player.connection", (PlayerInfo) object, "connect");
            sender.sendToTopicAsJson("lava.player.connection", playerNetActionVo, NetAction.CONNECT.getActionName());
        }
    }

    @After("logObsoletePlayerInfo()")
    public void logPlayerInfoDisconnect(JoinPoint joinPoint) {
        System.out.println("point cut player left");

        Object object = joinPoint.getArgs()[0];
        if(object instanceof PlayerInfo) {
            PlayerNetActionVo<PlayerInfo> playerNetActionVo = new PlayerNetActionVo<>();
            playerNetActionVo.setAction(NetAction.CONNECT);
            playerNetActionVo.setObject((PlayerInfo) object);
            playerNetActionVo.setTimestamp(System.currentTimeMillis());
            playerNetActionVo.setSuccess(true);
            sender.sendToTopic("lava.player.connection", (PlayerInfo) object, "disconnect");
            sender.sendToTopicAsJson("lava.player.connection", playerNetActionVo, NetAction.DISCONNECT.getActionName());
        }
    }
}
