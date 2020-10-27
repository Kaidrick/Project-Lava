package moe.ofs.backend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.gson.annotations.SerializedName;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@LuaState(Level.SERVER_POLL)
@Accessors(chain = true)
@TableName("EXPORT_OBJECT")
public class DcsExportObject extends DcsBaseEntity {

    @TableField("OWN_BANK")
    @SerializedName("Bank")
    private Double bank;

    @TableField("COALITION")
    @SerializedName("Coalition")
    private String coalition;

    @TableField("COALITION_ID")
    @SerializedName("CoalitionID")
    private int coalitionID;

    @TableField("COUNTRY_ID")
    @SerializedName("Country")
    private int country;

    @TableField("GROUP_NAME")
    @SerializedName("GroupName")
    private String groupName;

    @TableField("OWN_HEADING")
    @SerializedName("Heading")
    private Double heading;

    @TableField("OWN_NAME")
    @SerializedName("Name")
    private String name;

    @TableField("OWN_PITCH")
    @SerializedName("Pitch")
    private Double pitch;

    @TableField("RUNTIME_ID")
    @SerializedName("RuntimeID")
    private long runtimeID;

    @TableField("UNIT_NAME")
    @SerializedName("UnitName")
    private String unitName;

    private DcsPositionVector vector;

    private DcsGeoPosition position;

    /**
     * Two ExportObject is consider equal if runtime id and unitName is the same.
     * @param o the object to be tested equality with.
     * @return boolean value indicating whether two ExportObject are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DcsExportObject that = (DcsExportObject) o;

        if (runtimeID != that.runtimeID) return false;
        return Objects.equals(unitName, that.unitName);
    }

    @Override
    public int hashCode() {
        int result = (int) (runtimeID ^ (runtimeID >>> 32));
        result = 31 * result + (unitName != null ? unitName.hashCode() : 0);
        return result;
    }
}
