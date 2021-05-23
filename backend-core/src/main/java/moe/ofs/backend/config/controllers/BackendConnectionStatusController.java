package moe.ofs.backend.config.controllers;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.config.BackendOperatingStatusMonitorService;
import moe.ofs.backend.config.model.ConnectionInfoVo;
import moe.ofs.backend.connector.ConnectionManager;
import moe.ofs.backend.connector.LavaSystemStatus;
import moe.ofs.backend.dao.LogEntryDao;
import moe.ofs.backend.jms.Sender;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.web.bind.annotation.*;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController

// FIXME: very bad design, need refactoring
public class BackendConnectionStatusController {

    private final ConnectionManager manager;

    private final BackendOperatingStatusMonitorService statusMonitorService;

    private final LogEntryDao logEntryDao;

    private final AtomicInteger exportObjectCount = new AtomicInteger(0);

    private final Sender sender;

    // TODO: change to "in-game" player count in the future, because ED webGui already has this info
    private final AtomicInteger connectedPlayerCount = new AtomicInteger(0);

    public BackendConnectionStatusController(ConnectionManager manager,
                                             BackendOperatingStatusMonitorService statusMonitorService,
                                             LogEntryDao logEntryDao, Sender sender) {
        this.manager = manager;
        this.statusMonitorService = statusMonitorService;
        this.logEntryDao = logEntryDao;
        this.sender = sender;
    }

    @GetMapping("/status")
    public ConnectionInfoVo getBackendStatus() {
        ConnectionInfoVo status = new ConnectionInfoVo();
        status.setConnected(manager.isBackendConnected());
        status.setTimestamp(LocalDateTime.now());
        status.setPhaseCode(LavaSystemStatus.getPhase().getStatusCode());
        status.setTheater(LavaSystemStatus.getTheater());
        status.setObjectCount(exportObjectCount.get());
        status.setPlayerCount(connectedPlayerCount.get());

        return status;
    }

    @JmsListener(destination = "lava.spawn-control.export-object", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'spawn'")
    public void receiveUnitSpawnMessage(TextMessage message) throws JMSException {
        exportObjectCount.incrementAndGet();
    }

    @JmsListener(destination = "lava.spawn-control.export-object", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'depawn'")
    public void receiveUnitDespawnMessage(TextMessage message) throws JMSException {
        exportObjectCount.decrementAndGet();
    }

    @JmsListener(destination = "luap", containerFactory = "jmsQueueListenerContainerFactory",
            selector = "JMSType = 'heartbeat'")
    public void listenForLuaStateActivities(TextMessage textMessage) throws JMSException {
        log.info("heartbeat received: {}", textMessage.getText());
    }

    @JmsListener(destination = "luap", containerFactory = "jmsQueueListenerContainerFactory",
            selector = "action like 'dostring:%'")
    public void listenForActionReceipt(TextMessage textMessage) throws JMSException {
        log.info("Lua do string action receipt: {}", textMessage.getText());
    }

    @PostMapping("lua/msg-test")
    public void sendMessageToLuaStateStomp(@RequestBody String text) {
        sender.sendToQueueAsJson("luaq", text, "speaker");
    }

    @PostMapping("lua/action")
    public void testStringInLua(@RequestBody Map<String, Object> map) {
        String message = String.valueOf(map.get("luaString"));
        String action = String.valueOf(map.get("action"));
        String luaState = String.valueOf(map.get("luaState"));
        UUID messageId = UUID.randomUUID();
        Map<String, String> headers = new HashMap<>();
        headers.put("route", luaState);
        headers.put("action", action);
        headers.put("action-id", messageId.toString());
        sender.sendToQueue("luaq", message, headers);
    }

//    @GetMapping("syslog")
//    public List<LogEntry> getSystemLogs() {
//        return logEntryDao.selectList(null);
//    }
}
