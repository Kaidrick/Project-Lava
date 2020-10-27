package moe.ofs.backend.lavalog.eventlogger;

import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.lavalog.LavaLog;
import moe.ofs.backend.object.LogLevel;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.Serializable;

public class PlayerConnectionLogger {

    private final LavaLog.Logger logger = LavaLog.getLogger(SpawnControlLogger.class);

    @JmsListener(destination = "player.connection", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'connect'")
    private void logPlayerConnect(ObjectMessage objectMessage) throws JMSException {
        Serializable object = objectMessage.getObject();
        if(object instanceof PlayerInfo) {

            PlayerInfo playerInfo = (PlayerInfo) object;

            logger.log(LogLevel.INFO, String.format("Player <%s> connected from <%s> with <%s> client; ping %sms",
                    playerInfo.getName(), playerInfo.getIpaddr(), playerInfo.getLang(), playerInfo.getPing()));
        }
    }

    @JmsListener(destination = "player.connection", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'disconnect'")
    private void logPlayerDisconnect(ObjectMessage objectMessage) throws JMSException {
        Serializable object = objectMessage.getObject();
        if(object instanceof PlayerInfo) {

            PlayerInfo playerInfo = (PlayerInfo) object;

            logger.log(LogLevel.INFO, String.format("Player <%s> disconnected from server",
                    playerInfo.getName()));
        }
    }

    @JmsListener(destination = "player.connection", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'slotchange'")
    private void logPlayerSlotChange(ObjectMessage objectMessage) throws JMSException {
        Serializable object = objectMessage.getObject();
        if(object instanceof PlayerInfo[]) {

            PlayerInfo[] playerInfoArray = (PlayerInfo[]) object;
            PlayerInfo prev = playerInfoArray[0];
            PlayerInfo curr = playerInfoArray[1];

            logger.log(LogLevel.INFO, String.format("Player <%s> slot changed from %s to %s",
                    curr.getName(), prev.getSlot(), curr.getSlot()));
        }
    }

}
