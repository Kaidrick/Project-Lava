package moe.ofs.backend.function.newslotcontrol.model;

import moe.ofs.backend.hookinterceptor.HookInterceptorDefinition;
import moe.ofs.backend.hookinterceptor.HookType;
import moe.ofs.backend.services.MissionPersistenceService;

public class SlotChangeInterceptor extends HookInterceptorDefinition {
    public SlotChangeInterceptor(String name, String predicateFunction, MissionPersistenceService storage) {
        super(name, predicateFunction, storage, HookType.ON_PLAYER_TRY_CHANGE_SLOT);
    }
}
