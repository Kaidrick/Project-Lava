package moe.ofs.backend.object;

import moe.ofs.backend.domain.dcs.theater.Vector3D;

import java.io.Serializable;

/**
 * Beware that parking id start at 0 not 1
 */

public class Parking implements Serializable {
    private static final long serialVersionUID = 1L;

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
                airdromeName, id, getReadableTerminalType(), getReadableHeading());
    }

    private int id;
    private String airdromeName;
    private int airdromeId;
    private Vector3D position;
    private double northCorrection;
    private int terminalType;
    private Metadata metadata;


    private class Metadata implements Serializable {
        boolean TO_AC;
        int Term_Index_0;
        double fDistToRW;
    }

    private double initialHeading;

    public int getAirdromeId() {
        return airdromeId;
    }

    public void setAirdromeId(int airdromeId) {
        this.airdromeId = airdromeId;
    }

    public String getAirdromeName() {
        return airdromeName;
    }

    public void setAirdromeName(String airdromeName) {
        this.airdromeName = airdromeName;
    }

    public double getInitialHeading() {
        return initialHeading;
    }

    public void setInitialHeading(double initialHeading) {
        this.initialHeading = initialHeading;
    }

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(int terminalType) {
        this.terminalType = terminalType;
    }

    public double getNorthCorrection() {
        return northCorrection;
    }

    public void setNorthCorrection(double northCorrection) {
        this.northCorrection = northCorrection;
    }
}
