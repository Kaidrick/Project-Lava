package moe.ofs.backend.simevent.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moe.ofs.backend.domain.BaseEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SimEvent extends BaseEntity {
    @SerializedName("id")
    private int eventId;

    private long time;

    @SerializedName("initiator")
    private long initiatorId;

    @SerializedName("target")
    private long targetId;

    @SerializedName("weapon")
    private long weaponId;
}
