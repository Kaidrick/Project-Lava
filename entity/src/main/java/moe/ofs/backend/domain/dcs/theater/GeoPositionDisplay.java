package moe.ofs.backend.domain.dcs.theater;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GeoPositionDisplay {
    private String latitudeDirection;
    private String latitudeDegree;
    private String latitudeMinute;
    private String latitudeSecond;

    private String longitudeDirection;
    private String longitudeDegree;
    private String longitudeMinute;
    private String longitudeSecond;
}
