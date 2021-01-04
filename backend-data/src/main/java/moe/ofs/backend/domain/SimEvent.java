package moe.ofs.backend.domain;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import moe.ofs.backend.domain.BaseEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class SimEvent extends BaseEntity {
    @SerializedName("id")
    protected int eventId;  // TODO: replace with EventType enum

    protected double time;  // DCS event time may be decimals

    protected long initiatorId;

    protected long targetId;

    protected long weaponId;

    private transient boolean associated;
    private transient int associateRetryCount;

    public void setAssociated(boolean associated) {
        this.associated = associated;
    }

    public void incrementRetryCount() {
        associateRetryCount++;
    }
}
