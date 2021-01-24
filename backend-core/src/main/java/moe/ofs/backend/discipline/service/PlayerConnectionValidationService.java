package moe.ofs.backend.discipline.service;

import moe.ofs.backend.discipline.model.PlayerTryConnectRecord;
import moe.ofs.backend.hookinterceptor.HookInterceptorDefinition;
import moe.ofs.backend.hookinterceptor.HookInterceptorProcessService;

import java.util.List;
import java.util.Map;

public interface PlayerConnectionValidationService
        extends HookInterceptorProcessService<PlayerTryConnectRecord, HookInterceptorDefinition> {
    void blockPlayerUcid(String ucid);

    void blockPlayerUcid(String ucid, String reason);

    void blockPlayerUcid(List<String> ucidList);

    void blockPlayerUcid(Map<Object, String> ucidReasonMap);

    void unblockPlayerUcid(String ucid);
}
