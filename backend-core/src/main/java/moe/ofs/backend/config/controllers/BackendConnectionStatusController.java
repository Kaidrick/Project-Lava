package moe.ofs.backend.config.controllers;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.BackgroundTask;
import moe.ofs.backend.config.BackendOperatingStatusMonitorService;
import moe.ofs.backend.config.model.ConnectionInfoVo;
import moe.ofs.backend.dao.LogEntryDao;
import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.domain.LogEntry;
import moe.ofs.backend.util.ConnectionManager;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.web.bind.annotation.*;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController

// FIXME: very bad design, need refactoring
public class BackendConnectionStatusController {

    private final ConnectionManager manager;

    private final BackendOperatingStatusMonitorService statusMonitorService;

    private final LogEntryDao logEntryDao;

    private final AtomicInteger exportObjectCount = new AtomicInteger(0);

    // TODO: change to "in-game" player count in the future, because ED webGui already has this info
    private final AtomicInteger connectedPlayerCount = new AtomicInteger(0);

    public BackendConnectionStatusController(ConnectionManager manager,
                                             BackendOperatingStatusMonitorService statusMonitorService,
                                             LogEntryDao logEntryDao) {
        this.manager = manager;
        this.statusMonitorService = statusMonitorService;
        this.logEntryDao = logEntryDao;
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ConnectionInfoVo getBackendStatus() {
        ConnectionInfoVo status = new ConnectionInfoVo();
        status.setConnected(manager.isBackendConnected());
        status.setTimestamp(LocalDateTime.now());
        status.setPhaseCode(BackgroundTask.getCurrentTask().getPhase().getStatusCode());
        status.setTheater(BackgroundTask.getCurrentTask().getTaskDcsMapTheaterName());
        status.setObjectCount(exportObjectCount.get());
        status.setPlayerCount(connectedPlayerCount.get());

        return status;
    }

    @JmsListener(destination = "unit.spawncontrol", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'spawn'")
    public void receiveUnitSpawnMessage(ObjectMessage message) throws JMSException {
        Serializable object = message.getObject();
        if (object instanceof ExportObject) {
            exportObjectCount.incrementAndGet();
        }
    }

    @JmsListener(destination = "unit.spawncontrol", containerFactory = "jmsListenerContainerFactory", selector = "type = 'depawn'")
    public void receiveUnitDespawnMessage(ObjectMessage message) throws JMSException {
        Serializable object = message.getObject();
        if (object instanceof ExportObject) {
            exportObjectCount.decrementAndGet();
        }
    }

//    @GetMapping("syslog")
//    public List<LogEntry> getSystemLogs() {
//        return logEntryDao.selectList(null);
//    }
}
