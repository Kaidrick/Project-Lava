package moe.ofs.backend.function.newslotcontrol.services;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.domain.PlayerInfo;

import java.util.Optional;

public interface SlotManageService {
    void forceSlot(PlayerInfo playerInfo, String slotId);

    Optional<ExportObject> getSlotExportObject(String slotId);

    void lockSlot(String slotId);

    void releaseSlot(String slotId);

    void emptySlot(String slotId);

    void emptyAllSlots();
}
