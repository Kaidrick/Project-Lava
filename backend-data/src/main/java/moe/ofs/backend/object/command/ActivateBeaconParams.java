package moe.ofs.backend.object.command;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateBeaconParams {
    private int type;

    @SerializedName("AA")
    private boolean airborne;

    private String callsign;

    private String modeChannel;

    private int channel;

    private int system;

    private int unitId;

    private boolean bearing;

    private long frequency;
}
