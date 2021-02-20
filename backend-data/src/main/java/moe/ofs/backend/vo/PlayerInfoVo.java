package moe.ofs.backend.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.experimental.Accessors;
import moe.ofs.backend.domain.connector.Level;
import moe.ofs.backend.domain.dcs.LuaState;

import java.io.Serializable;
import java.util.Objects;

@Data
@LuaState(Level.SERVER_POLL)
@Accessors(chain = true)
@TableName("PLAYER_INFO")
public class PlayerInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("IPADDR")
    private String ipaddr;

    @TableField("LANG")
    private String lang;

    @TableField("NAME")
    private String name;

    @TableField("NET_ID")
    @SerializedName("id")
    private Integer netId;

    @TableField("PILOT_ID")
    @SerializedName("pilotid")
    private Long pilotId;

    @TableField("PING")
    private Integer ping;

    @TableField("SIDE")
    private Integer side;

    @TableField("SLOT")
    private String slot;

    @TableField("STARTED")
    private Boolean started;

    @TableField("UCID")
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

        PlayerInfoVo that = (PlayerInfoVo) o;

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
