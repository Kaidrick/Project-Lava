package moe.ofs.backend.function.markpanel;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import moe.ofs.backend.object.Vector3D;

/**
 * The y value of Vector3D position will be snapped to ground altitude if its value is below ground level
 *
 */

@Getter
@Setter
@Builder
public final class MarkPanel {
    @SerializedName("idx")
    private long index;

    @SerializedName("groupID")
    private long groupId;

    @SerializedName("pos")
    private Vector3D position;

    @SerializedName("text")
    private String content;

    private boolean readOnly;

    private String messageOnCreation;

    private String author;

    @SerializedName("time")
    private transient long creationTime;

    private transient long coalition;
}
