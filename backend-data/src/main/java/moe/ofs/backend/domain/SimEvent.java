package moe.ofs.backend.domain;

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
    protected int eventId;

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
