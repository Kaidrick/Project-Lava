package moe.ofs.backend.function;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlotEntryRequest {
    @SerializedName("player_id")
    private int netId;

    private int side;

    @SerializedName("slot_id")
    private String slotId;
}