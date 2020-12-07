package moe.ofs.backend.function.newslotcontrol.services;

import moe.ofs.backend.function.newslotcontrol.model.SlotChangeData;
import moe.ofs.backend.hookinterceptor.HookInterceptorProcessService;

import java.util.List;

public interface SlotChangeProcessService {
    List<SlotChangeData> poll();
}
