package moe.ofs.backend.function.newslotcontrol.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import moe.ofs.backend.hookinterceptor.HookProcessEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class PlayerSlotChangeRecord extends HookProcessEntity {
    private int side;

    @SerializedName("cur_side")
    private int currentSide;

    @SerializedName("slot_id")
    private String slotId;

    @SerializedName("cur_slot_id")
    private String currentSlotId;
}
