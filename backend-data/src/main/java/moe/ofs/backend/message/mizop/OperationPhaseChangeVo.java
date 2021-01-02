package moe.ofs.backend.message.mizop;

import lombok.Getter;
import lombok.Setter;
import moe.ofs.backend.message.OperationPhase;

@Getter
@Setter
public class OperationPhaseChangeVo {
    private long timestamp;
    private OperationPhase previous;
    private String previousPhaseName;
    private OperationPhase current;
    private String currentPhaseName;
}
