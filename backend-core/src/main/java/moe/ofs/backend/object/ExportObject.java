package moe.ofs.backend.object;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@Entity
public final class ExportObject extends BaseEntity {
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

    @Builder
    public ExportObject(Long id, double bank, String coalition, int coalitionID, int country, String groupName, double heading,
                        String name, double pitch, int runtimeID, String unitName,
                        Map<String, Boolean> flags, Map<String, Double> latLongAlt,
                        Map<String, Double> position, Map<String, Integer> type) {
        super(id);
        this.Bank = bank;
        this.Coalition = coalition;
        this.CoalitionID = coalitionID;
        this.Country = country;
        this.GroupName = groupName;
        this.Heading = heading;
        this.Name = name;
        this.Pitch = pitch;
        this.RuntimeID = runtimeID;
        this.UnitName = unitName;

        this.Flags = flags;
        this.LatLongAlt = latLongAlt;
        this.Position = position;
        this.Type = type;
    }

    @ElementCollection
    @MapKeyColumn(name="key")
    @Column(name="value")
    @CollectionTable(name="flags", joinColumns=@JoinColumn(name="id"))
    private Map<String, Boolean> Flags;

    @ElementCollection
    @MapKeyColumn(name="key")
    @Column(name="value")
    @CollectionTable(name="lat_lon_alt", joinColumns=@JoinColumn(name="id"))
    private Map<String, Double> LatLongAlt;

    @ElementCollection
    @MapKeyColumn(name="key")
    @Column(name="value")
    @CollectionTable(name="position", joinColumns=@JoinColumn(name="id"))
    private Map<String, Double> Position;

    @ElementCollection
    @MapKeyColumn(name="key")
    @Column(name="value")
    @CollectionTable(name="type", joinColumns=@JoinColumn(name="id"))
    private Map<String, Integer> Type;
}
