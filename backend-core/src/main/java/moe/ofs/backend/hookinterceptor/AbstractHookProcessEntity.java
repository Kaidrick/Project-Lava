package moe.ofs.backend.hookinterceptor;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.ofs.backend.domain.PlayerInfo;

@Data
@EqualsAndHashCode
public class AbstractHookProcessEntity {
    @SerializedName("playerID")
    private int netId;  // net of the player who sent this message

    private double time;  // timestamp on message sent

    private transient PlayerInfo player;
}
