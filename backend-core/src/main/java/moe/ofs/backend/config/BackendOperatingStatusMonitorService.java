package moe.ofs.backend.config;

import moe.ofs.backend.domain.connector.OperationPhase;

//
public interface BackendOperatingStatusMonitorService {
    void change(OperationPhase status);

    
}
