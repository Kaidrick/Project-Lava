package moe.ofs.backend.config;

import moe.ofs.backend.config.model.ConnectionInfoVo;
import moe.ofs.backend.jms.Sender;
import moe.ofs.backend.domain.connector.OperationPhase;
import org.springframework.stereotype.Service;

@Service
public class BackendOperatingStatusMonitorServiceImpl implements BackendOperatingStatusMonitorService {
    private final Sender sender;

    private OperationPhase phase;

    public BackendOperatingStatusMonitorServiceImpl(Sender sender) {
        this.sender = sender;

        phase = OperationPhase.IDLE;
    }

    @Override
    public void change(OperationPhase phase) {
        this.phase = phase;
        ConnectionInfoVo connectionInfoVo = new ConnectionInfoVo();
        connectionInfoVo.setPhaseCode(phase.getStatusCode());
        sender.sendToTopicAsJson("greetings", connectionInfoVo, "type");
    }
}
