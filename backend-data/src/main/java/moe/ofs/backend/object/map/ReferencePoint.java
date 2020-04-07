package moe.ofs.backend.object.map;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import moe.ofs.backend.domain.BaseEntity;

import java.util.Map;

@Getter
@Setter
@Builder
@ToString
public class ReferencePoint extends BaseEntity {
    @SerializedName("callsignStr")
    private String name;

    @SerializedName("comment")
    private String description;

    @SerializedName("id")
    private long index;

    private double x;

    private double y;

    private Map<String, Number> properties;
}
