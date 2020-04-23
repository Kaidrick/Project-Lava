package moe.ofs.backend.object;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class SpawnInfo {
    @SerializedName("group_runtime_id")
    private int groupId;

    @SerializedName("group_unit_name_id_pairs")
    private Map<String, Integer> units;
}
