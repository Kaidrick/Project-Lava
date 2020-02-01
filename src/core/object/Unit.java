package core.object;

import java.util.List;

public class Unit extends SimObject {

    private List<AmmoData>      ammo;
    private List<Vector3D>      att;
    private String              callsign;
    private int                 country;
    private double              fuel;
    private double              nc;
    private boolean             player_control;
    private Vector3D            pos;
    private long                runtime_id;
    private String              type;
    private Vector3D            velocity;
    private Coord               coord;

    public Vector3D getPos() {
        return pos;
    }

    public List<AmmoData> getAmmo() {
        return ammo;
    }

    public List<Vector3D> getAtt() {
        return att;
    }

    public String getCallsign() {
        return callsign;
    }

    public int getCountry() {
        return country;
    }

    public double getFuel() {
        return fuel;
    }

    public double getNc() {
        return nc;
    }

    public boolean isPlayer_control() {
        return player_control;
    }

    public long getRuntime_id() {
        return runtime_id;
    }

    public String getType() {
        return type;
    }

    public Vector3D getVelocity() {
        return velocity;
    }

    public Coord getCoord() {
        return coord;
    }
}
