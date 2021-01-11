package moe.ofs.backend.domain.dcs.theater;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import moe.ofs.backend.domain.dcs.BaseEntity;
import moe.ofs.backend.domain.dcs.poll.ExportObject;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GeoPosition extends BaseEntity implements Serializable {

    @OneToOne(mappedBy = "geoPosition")
    private ExportObject exportObject;

    @SerializedName("Lat")
    private double latitude;

    @SerializedName("Long")
    private double longitude;

    @SerializedName("Alt")
    private double altitude;

    public GeoPosition(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GeoPosition(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public GeoPosition(GeoPosition geoPosition) {
        this.exportObject = geoPosition.exportObject;
        this.latitude = geoPosition.getLatitude();
        this.longitude = geoPosition.getLongitude();
        this.altitude = geoPosition.getAltitude();
    }

    @Override
    public String toString() {
        // output should be in some what readable format
        // dd mm ss to be exact
        return GeoPositions.toLatLonAltDisplay(this);
    }
}
