package moe.ofs.backend.hookinterceptor;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.ofs.backend.domain.PlayerInfo;

@Data
@EqualsAndHashCode
public class HookProcessEntity {
    @SerializedName("__entity_target")
    private HookType hookType;

    @SerializedName("__entity_definition_name")
    private String definitionName;

    @SerializedName("__entity_player_id")
    private int netId;  // net of the player who sent this message

    private Boolean isAllowed;  // true for allowed, false for rejected, null for next chain

    @SerializedName("__entity_time")
    private double time;  // timestamp on message sent

    @SerializedName("__predicate_result")
    private Object result;

    private Object meta;  // can be used to pass extra information

    private transient PlayerInfo player;  // used to associate an existing player using PlayerInfoService
}
