package moe.ofs.backend.object;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import moe.ofs.backend.domain.BaseEntity;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class ParkingInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private int parkingId;
    private String airdromeName;
    private int airdromeId;
    private Vector3D position;
    private double northCorrection;
    private int terminalType;
    private double initialHeading;
    private ParkingInfo.Metadata metadata;

    private class Metadata implements Serializable {
        boolean TO_AC;
        int Term_Index_0;
        double fDistToRW;
    }

    public String getReadableTerminalType() {
        return terminalType == 16 ? "Runway Takeoff Point"
                : terminalType == 40 ? "Helicopter Only Parking" : "Normal Parking";
    }

    public double getReadableHeading() {
        // initial heading is between [-pi, pi]
        return Math.toDegrees(initialHeading + Math.PI);
    }

    public String toString() {
        return super.toString() + "|" + String.format("%s Parking #%d %s Init HDG %f",
                airdromeName, parkingId, getReadableTerminalType(), getReadableHeading());
    }
}
