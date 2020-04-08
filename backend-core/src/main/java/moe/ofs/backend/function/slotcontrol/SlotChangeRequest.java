package moe.ofs.backend.function.slotcontrol;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlotChangeRequest {
    @SerializedName("player_id")
    private int netId;

    private int side;

    @SerializedName("cur_side")
    private int currentSide;

    @SerializedName("slot_id")
    private String slotId;

    @SerializedName("cur_slot_id")
    private String currentSlotId;
}