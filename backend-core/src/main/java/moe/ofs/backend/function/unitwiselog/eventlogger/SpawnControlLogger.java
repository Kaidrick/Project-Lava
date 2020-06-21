package moe.ofs.backend.function.unitwiselog.eventlogger;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.function.unitwiselog.LogControl;
import moe.ofs.backend.logmanager.Level;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.Serializable;

public class SpawnControlLogger {

    private final LogControl.Logger logger = LogControl.getLogger(SpawnControlLogger.class);

    @JmsListener(destination = "unit.spawncontrol", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'spawn'")
    private void logUnitSpawn(ObjectMessage objectMessage) throws JMSException {
        Serializable object = objectMessage.getObject();
        if(object instanceof ExportObject) {

            ExportObject exportObject = (ExportObject) object;

            logger.log(Level.INFO, String.format("Unit Spawn: %s (RuntimeID: %s) - %s Type",
                    exportObject.getUnitName(), exportObject.getRuntimeID(), exportObject.getName()));
        }
    }

    @JmsListener(destination = "unit.spawncontrol", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'despawn'")
    private void logUnitDespawn(ObjectMessage objectMessage) throws JMSException {
        Serializable object = objectMessage.getObject();
        if(object instanceof ExportObject) {

            ExportObject exportObject = (ExportObject) object;

            logger.log(Level.INFO, String.format("Unit Despawn: %s (RuntimeID: %s) - %s Type",
                    exportObject.getUnitName(), exportObject.getRuntimeID(), exportObject.getName()));
        }
    }

}
