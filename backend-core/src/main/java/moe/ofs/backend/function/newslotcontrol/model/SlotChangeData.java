package moe.ofs.backend.function.newslotcontrol.model;

import com.google.gson.annotations.SerializedName;
import moe.ofs.backend.hookinterceptor.HookProcessEntity;

public class SlotChangeData extends HookProcessEntity {
    private int side;

    @SerializedName("cur_side")
    private int currentSide;

    @SerializedName("slot_id")
    private String slotId;

    @SerializedName("cur_slot_id")
    private String currentSlotId;
}
