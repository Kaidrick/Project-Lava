package moe.ofs.backend.function.newslotcontrol.services;

import moe.ofs.backend.domain.ExportObject;

public interface SlotManageService {
    void forceSlot(int id);

    ExportObject getSlotExportObject(int id);

    void lockSlot(int id);

    void releaseSlot(int id);

    void cleanSlot(int id);

    void cleanAllSlots();
}
