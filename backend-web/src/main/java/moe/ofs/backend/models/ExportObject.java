package moe.ofs.backend.models;

import java.util.Map;

public class ExportObject extends SimObject {
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

    public void setBank(double bank) {
        Bank = bank;
    }

    public String getCoalition() {
        return Coalition;
    }

    public void setCoalition(String coalition) {
        Coalition = coalition;
    }

    public int getCoalitionID() {
        return CoalitionID;
    }

    public void setCoalitionID(int coalitionID) {
        CoalitionID = coalitionID;
    }

    public int getCountry() {
        return Country;
    }

    public void setCountry(int country) {
        Country = country;
    }

    public Map<String, Boolean> getFlags() {
        return Flags;
    }

    public void setFlags(Map<String, Boolean> flags) {
        Flags = flags;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public double getHeading() {
        return Heading;
    }

    public void setHeading(double heading) {
        Heading = heading;
    }

    public Map<String, Double> getLatLongAlt() {
        return LatLongAlt;
    }

    public void setLatLongAlt(Map<String, Double> latLongAlt) {
        LatLongAlt = latLongAlt;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public void setName(String name) {
        Name = name;
    }

    public double getPitch() {
        return Pitch;
    }

    public void setPitch(double pitch) {
        Pitch = pitch;
    }

    public Map<String, Double> getPosition() {
        return Position;
    }

    public void setPosition(Map<String, Double> position) {
        Position = position;
    }

    public int getRuntimeID() {
        return RuntimeID;
    }

    public void setRuntimeID(int runtimeID) {
        RuntimeID = runtimeID;
    }

    public Map<String, Integer> getType() {
        return Type;
    }

    public void setType(Map<String, Integer> type) {
        Type = type;
    }

    public String getUnitName() {
        return UnitName;
    }

    public void setUnitName(String unitName) {
        UnitName = unitName;
    }
}
