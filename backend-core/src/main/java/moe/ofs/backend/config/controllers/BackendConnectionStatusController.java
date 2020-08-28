package moe.ofs.backend.config.controllers;

import moe.ofs.backend.config.model.ConnectionStatus;
import moe.ofs.backend.util.ConnectionManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class BackendConnectionStatusController {

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ConnectionStatus getBackendStatus() {
        ConnectionStatus status = new ConnectionStatus();
        status.setConnected(ConnectionManager.getInstance().isBackendConnected());
        status.setTimestamp(LocalDateTime.now());

        return status;
    }
}
