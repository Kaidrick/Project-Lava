package moe.ofs.backend.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@LuaState(Level.SERVER_POLL)
@Entity
@Table(name = "player_info")
public class PlayerInfo extends BaseEntity {
    private static final String emptySlotAltName = "Observer";

    @SerializedName("pilotid")
    private long pilotId;  // what is this for?

    @SerializedName("id")
    private int netId;
    private String name;
    private String ipaddr;
    private String lang;
    private int ping;
    private int side;
    private String slot;
    private boolean started;  // what is this?
    private String ucid;

    @Override
    public String toString() {
        String slot = getSlot();
        return String.format("Player <%s>(%s) Slot <%s> using [%s] Client @ %s",
                name, ucid, slot, lang.toUpperCase(), ipaddr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerInfo that = (PlayerInfo) o;

        if (!Objects.equals(name, that.name)) return false;
        return ucid.equals(that.ucid);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ucid.hashCode();
        return result;
    }
}
