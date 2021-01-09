package moe.ofs.backend.lavalog.eventlogger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.LavaLog;
import moe.ofs.backend.domain.behaviors.spawnctl.SpawnControlVo;
import moe.ofs.backend.domain.jms.LogLevel;
import moe.ofs.backend.object.StaticObject;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.lang.reflect.Type;

@Slf4j
public class StaticObjectLogger {

    private final LavaLog.Logger logger = LavaLog.getLogger(SpawnControlLogger.class);

    @JmsListener(destination = "lava.spawn-control.static-object", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'add'")
    private void logStaticObjectAdd(TextMessage textMessage) throws JMSException {
        StaticObject staticObject = parse(textMessage);


        logger.log(LogLevel.INFO,
                String.format("Adding Static Object [%s] spawned with runtime id [%s] in livery [%s]",
                        staticObject.getType(), staticObject.getId(), staticObject.getLivery_id()));
    }

    @JmsListener(destination = "lava.spawn-control.static-object", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'remove'")
    private void logStaticObjectRemove(TextMessage textMessage) throws JMSException {
        StaticObject staticObject = parse(textMessage);


        logger.log(LogLevel.INFO,
                String.format("Removing Static Object with runtime id [%s]", staticObject.getId()));
    }

    private StaticObject parse(TextMessage textMessage) throws JMSException {
        String jsonString = textMessage.getText();

        Type type = new TypeToken<SpawnControlVo<StaticObject>>() {}.getType();
        SpawnControlVo<StaticObject> spawnControlVo = new Gson().fromJson(jsonString, type);

        return spawnControlVo.getObject();
    }
}
