package moe.ofs.backend;

import moe.ofs.backend.object.map.GeoPosition;
import moe.ofs.backend.object.map.GeoPositions;
import moe.ofs.backend.object.map.Orientation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeoPositionsTest {
    GeoPosition geoPosition;
    GeoPosition compareGeoPosition;

    @BeforeEach
    void setUp() {
        geoPosition = new GeoPosition();
        geoPosition.setLatitude(64.698056);
        geoPosition.setLongitude(-110.609167);
        geoPosition.setAltitude(17);

        compareGeoPosition = GeoPositions.get(
                Orientation.NORTH,"64", "41", "53",
                Orientation.WEST,"110", "36", "33");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void latLon() {
//        System.out.println("GeoPositions.toLatLonDisplay(geoPosition) = " + GeoPositions.toLatLonDisplay(geoPosition));
//        System.out.println("GeoPositions.toLatLonAltDisplay(geoPosition) = " + GeoPositions.toLatLonAltDisplay(geoPosition));
    }

    @Test
    void formatStringArray() {
//        System.out.println(Arrays.toString(GeoPositions.formatStringArray(geoPosition, false)));
//        System.out.println(Arrays.toString(GeoPositions.formatStringArray(compareGeoPosition, false)));
    }
}