package moe.ofs.backend.jms;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.ExportObject;
import org.apache.http.annotation.Obsolete;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.Serializable;

@Slf4j
@Component
@Obsolete
public class UnitSpawnMessageReceiver {
//    @JmsListener(destination = "unit.spawncontrol", containerFactory = "jmsListenerContainerFactory",
//            selector = "type = 'spawn'")
    public void receive(ObjectMessage message) throws JMSException {
        Serializable object = message.getObject();
        if(object instanceof ExportObject) {
            log.info(((ExportObject) object).getRuntimeID() + " spawned");
        }
    }
}
