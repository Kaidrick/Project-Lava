package moe.ofs.backend.object.map;

import lombok.Data;

@Data
public class GeoPosition {
    private double latitude;
    private double longitude;
    private double altitude;
}
