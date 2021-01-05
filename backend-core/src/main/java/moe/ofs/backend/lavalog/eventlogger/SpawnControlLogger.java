package moe.ofs.backend.lavalog.eventlogger;

import com.google.gson.Gson;
import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.LavaLog;
import moe.ofs.backend.object.LogLevel;
import moe.ofs.backend.object.StaticObject;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import java.io.Serializable;

public class SpawnControlLogger {

    private final LavaLog.Logger logger = LavaLog.getLogger(SpawnControlLogger.class);

    @JmsListener(destination = "lava.spawn-control.export-object", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'spawn'")
    private void logUnitSpawn(TextMessage textMessage) throws JMSException {
        ExportObject exportObject = new Gson().fromJson(textMessage.getText(), ExportObject.class);

        if (exportObject != null) {
            logger.log(LogLevel.INFO, String.format("Unit Spawn: %s (RuntimeID: %s) - %s Type",
                    exportObject.getUnitName(), exportObject.getRuntimeID(), exportObject.getName()));

        }
    }

    @JmsListener(destination = "lava.spawn-control.export-object", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'despawn'")
    private void logUnitDespawn(TextMessage textMessage) throws JMSException {
        ExportObject exportObject = new Gson().fromJson(textMessage.getText(), ExportObject.class);

        if (exportObject != null) {
            logger.log(LogLevel.INFO, String.format("Unit Despawn: %s (RuntimeID: %s) - %s Type",
                    exportObject.getUnitName(), exportObject.getRuntimeID(), exportObject.getName()));

        }
    }

}
