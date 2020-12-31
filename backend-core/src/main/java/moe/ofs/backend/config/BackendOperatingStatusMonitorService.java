package moe.ofs.backend.config;

import moe.ofs.backend.message.OperationPhase;

//
public interface BackendOperatingStatusMonitorService {
    void change(OperationPhase status);

    
}
