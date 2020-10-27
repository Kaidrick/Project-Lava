package moe.ofs.backend.domain;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DcsGeoPosition extends DcsBaseEntity {

    @SerializedName("Lat")
    private double latitude;

    @SerializedName("Long")
    private double longitude;

    @SerializedName("Alt")
    private double altitude;
}
