package moe.ofs.backend.object.map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class GeoPosition implements Serializable {
    private double latitude;
    private double longitude;
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

    @Override
    public String toString() {
        // output should be in some what readable format
        // dd mm ss to be exact
        return GeoPositions.toLatLonAltDisplay(this);
    }
}
