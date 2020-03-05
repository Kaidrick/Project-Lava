package moe.ofs.backend.object;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ExportObjectWrapper {

    private double Bank;

    private String Coalition;

    private int CoalitionID;

    private int Country;

    private String GroupName;

    private double Heading;

    private String Name;

    private double Pitch;

    private int RuntimeID;

    private String UnitName;

    private Map<String, Boolean> Flags;

    private Map<String, Double> LatLongAlt;

    private Map<String, Double> Position;

    private Map<String, Integer> Type;
}
