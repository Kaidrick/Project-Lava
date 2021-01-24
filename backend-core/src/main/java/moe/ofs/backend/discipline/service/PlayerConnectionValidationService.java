package moe.ofs.backend.discipline.service;

import moe.ofs.backend.discipline.model.PlayerTryConnectRecord;
import moe.ofs.backend.hookinterceptor.HookInterceptorDefinition;
import moe.ofs.backend.hookinterceptor.HookInterceptorProcessService;

public interface PlayerConnectionValidationService
        extends HookInterceptorProcessService<PlayerTryConnectRecord, HookInterceptorDefinition> {
    void blockPlayerUcid(String ucid);

    void blockPlayerUcid(String ucid, String reason);

    void unblockPlayerUcid(String ucid);
}
