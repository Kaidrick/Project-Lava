package moe.ofs.backend.lavalog.eventlogger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.behaviors.net.PlayerNetActionVo;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;
import moe.ofs.backend.LavaLog;
import moe.ofs.backend.domain.jms.LogLevel;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.lang.reflect.Type;

@Slf4j
public class PlayerConnectionLogger {

    private final LavaLog.Logger logger = LavaLog.getLogger(PlayerConnectionLogger.class);

    private PlayerInfo parse(String json) {
        Type type = new TypeToken<PlayerNetActionVo<PlayerInfo>>() {}.getType();
        PlayerNetActionVo<PlayerInfo> playerNetActionVo = new Gson().fromJson(json, type);
        return playerNetActionVo.getObject();
    }

    private PlayerInfo[] parseSlotChange(String json) {
        Type type = new TypeToken<PlayerNetActionVo<PlayerInfo[]>>() {}.getType();
        PlayerNetActionVo<PlayerInfo[]> playerNetActionVo = new Gson().fromJson(json, type);
        return playerNetActionVo.getObject();
    }

    @JmsListener(destination = "lava.player.connection", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'connect'")
    private void logPlayerConnect(TextMessage textMessage) throws JMSException {
        PlayerInfo playerInfo = parse(textMessage.getText());

        log.info("connect -> " + playerInfo.toString());
        logger.log(LogLevel.INFO, String.format("Player <%s> connected from <%s> with <%s> client; ping %sms",
                playerInfo.getName(), playerInfo.getIpaddr(), playerInfo.getLang(), playerInfo.getPing()));
    }

    @JmsListener(destination = "lava.player.connection", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'disconnect'")
    private void logPlayerDisconnect(TextMessage textMessage) throws JMSException {
        PlayerInfo playerInfo = parse(textMessage.getText());

        log.info("disconnect -> " + playerInfo.toString());
        logger.log(LogLevel.INFO, String.format("Player <%s> disconnected from server",
                playerInfo.getName()));
    }

    @JmsListener(destination = "lava.player.connection", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'change-slot'")
    private void logPlayerSlotChange(TextMessage textMessage) throws JMSException {
        PlayerInfo[] change = parseSlotChange(textMessage.getText());

        PlayerInfo prev = change[0];
        PlayerInfo curr = change[1];

        log.info("Player <{}>({}) change slot from [{}] to [{}]",
                curr.getName(), curr.getUcid(), prev.getSlot(), curr.getSlot());

        logger.log(LogLevel.INFO, String.format("Player <%s> slot changed from %s to %s",
                curr.getName(), prev.getSlot(), curr.getSlot()));
    }

}
