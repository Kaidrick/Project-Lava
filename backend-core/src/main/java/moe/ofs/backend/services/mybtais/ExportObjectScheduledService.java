package moe.ofs.backend.services.mybtais;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.ExportObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@EnableScheduling
@Service
public class ExportObjectScheduledService {
    @Autowired
    private AsyncCollectionJob job;
    private Map<Long, ExportObject> newMap;
    private Map<Long, ExportObject> oldMap;
    private boolean init;

    @JmsListener(destination = "unit.spawncontrol", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'update'")
    private void logUnitSpawn(ObjectMessage objectMessage) throws JMSException {

        if (init) {
            init = false;
            job.collectDataToDB(newMap, oldMap);

            oldMap.clear();
            oldMap.putAll(newMap);
            newMap.clear();
        }

        Serializable object = objectMessage.getObject();
        if (object instanceof ExportObject) {
            ExportObject exportObject = (ExportObject) object;
            newMap.put(exportObject.getRuntimeID(), exportObject);
        }
    }

    @Scheduled(fixedRate = 10000)
    private void setInit() {

        if (newMap == null) {
            init = false;
            newMap = new HashMap<>();
            oldMap = new HashMap<>();
            return;
        }

        init = true;
    }
}
