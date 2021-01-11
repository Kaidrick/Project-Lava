package moe.ofs.backend.domain.message.mizop;

import lombok.Getter;
import lombok.Setter;
import moe.ofs.backend.domain.connector.OperationPhase;

@Getter
@Setter
public class OperationPhaseChangeVo {
    private long timestamp;
    private OperationPhase previous;
    private String previousPhaseName;
    private OperationPhase current;
    private String currentPhaseName;
}
