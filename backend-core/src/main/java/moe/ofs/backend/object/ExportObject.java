package moe.ofs.backend.object;

import lombok.Data;

import java.util.Map;

@Data
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
}
