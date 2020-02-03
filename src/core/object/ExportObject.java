package core.object;

import java.util.HashMap;
import java.util.Map;

public final class ExportObject {
    private double Bank;
    private String Coalition;
    private int CoalitionID;
    private int Country;
    private Map<String, Boolean> Flags;
    private String GroupName;
    private double Heading;
    private Map<String, Double> LatLongAlt;
    private String Name;
    private double Pitch;
    private Map<String, Double> Position;
    private int RuntimeID;
    private Map<String, Integer> Type;
    private String UnitName;

    public double getBank() {
        return Bank;
    }

    public String getCoalition() {
        return Coalition;
    }

    public int getCoalitionID() {
        return CoalitionID;
    }

    public int getCountry() {
        return Country;
    }

    public Map<String, Boolean> getFlags() {
        return new HashMap<>(Flags);
    }

    public String getGroupName() {
        return GroupName;
    }

    public double getHeading() {
        return Heading;
    }

    public Map<String, Double> getLatLongAlt() {
        return new HashMap<>(LatLongAlt);
    }

    public String getName() {
        return Name;
    }

    public double getPitch() {
        return Pitch;
    }

    public Map<String, Double> getPosition() {
        return new HashMap<>(Position);
    }

    public int getRuntimeID() {
        return RuntimeID;
    }

    public String getUnitName() {
        return UnitName;
    }
}
