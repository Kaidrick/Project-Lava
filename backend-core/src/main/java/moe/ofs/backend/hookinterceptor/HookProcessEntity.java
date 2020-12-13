package moe.ofs.backend.hookinterceptor;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.ofs.backend.domain.PlayerInfo;

@Data
@EqualsAndHashCode
public class HookProcessEntity {
    private HookType hookType;
    private String definitionName;

    @SerializedName("playerID")
    private int netId;  // net of the player who sent this message

    private Boolean isAllowed;  // true for allowed, false for rejected, null for next chain
    private double time;  // timestamp on message sent
    private Object meta;  // can be used to pass extra information

    private transient PlayerInfo player;  // used to associate an existing player using PlayerInfoService
}
