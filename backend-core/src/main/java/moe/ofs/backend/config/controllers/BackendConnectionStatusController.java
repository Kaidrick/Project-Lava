package moe.ofs.backend.config.controllers;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.BackgroundTask;
import moe.ofs.backend.config.model.ConnectionStatus;
import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.util.ConnectionManager;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
public class BackendConnectionStatusController {

    private AtomicInteger exportObjectCount = new AtomicInteger(0);
    private AtomicInteger inGamePlayerCount = new AtomicInteger(0);

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ConnectionStatus getBackendStatus() {
        ConnectionStatus status = new ConnectionStatus();
        status.setConnected(ConnectionManager.getInstance().isBackendConnected());
        status.setTimestamp(LocalDateTime.now());
        status.setPhaseCode(BackgroundTask.getCurrentTask().getPhase().getStatusCode());
        status.setTheater(BackgroundTask.getCurrentTask().getTaskDcsMapTheaterName());
        status.setObjectCount(exportObjectCount.get());

        return status;
    }

    //    @JmsListener(destination = "unit.spawncontrol", containerFactory = "jmsListenerContainerFactory",
//            selector = "type = 'spawn'")\
    @JmsListener(destination = "unit.spawncontrol", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'spawn'")
    public void receive(ObjectMessage message) throws JMSException {
        Serializable object = message.getObject();
        if(object instanceof ExportObject) {
            exportObjectCount.incrementAndGet();
        }
    }
}
