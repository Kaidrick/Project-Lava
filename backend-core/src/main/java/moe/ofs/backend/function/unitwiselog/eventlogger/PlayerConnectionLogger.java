package moe.ofs.backend.function.unitwiselog.eventlogger;

import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.function.unitwiselog.LogControl;
import moe.ofs.backend.logmanager.Level;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.Serializable;

public class PlayerConnectionLogger {

    private final LogControl.Logger logger = LogControl.getLogger(SpawnControlLogger.class);

    @JmsListener(destination = "player.connection", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'connect'")
    private void logPlayerConnect(ObjectMessage objectMessage) throws JMSException {
        Serializable object = objectMessage.getObject();
        if(object instanceof PlayerInfo) {

            PlayerInfo playerInfo = (PlayerInfo) object;

            logger.log(Level.INFO, String.format("Player <%s> connected from <%s> with <%s> client; ping %sms",
                    playerInfo.getName(), playerInfo.getIpaddr(), playerInfo.getLang(), playerInfo.getPing()));
        }
    }

    @JmsListener(destination = "player.connection", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'disconnect'")
    private void logPlayerDisconnect(ObjectMessage objectMessage) throws JMSException {
        Serializable object = objectMessage.getObject();
        if(object instanceof PlayerInfo) {

            PlayerInfo playerInfo = (PlayerInfo) object;

            logger.log(Level.INFO, String.format("Player <%s> disconnected from server",
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

            logger.log(Level.INFO, String.format("Player <%s> slot changed from %s to %s",
                    curr.getName(), prev.getSlot(), curr.getSlot()));
        }
    }

}
