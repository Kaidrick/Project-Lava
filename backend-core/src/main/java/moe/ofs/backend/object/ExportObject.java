package moe.ofs.backend.object;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "export_objects")
public final class ExportObject extends BaseEntity {
    @Column(name = "own_bank")
    private double bank;

    @Column(name = "coalition")
    private String coalition;

    @Column(name = "coalition_id")
    private int coalitionID;

    @Column(name = "country_id")
    private int country;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "own_heading")
    private double heading;

    @Column(name = "own_name")
    private String name;

    @Column(name = "own_pitch")
    private double pitch;

    @Column(name = "runtime_id")
    private long runtimeID;

    @Column(name = "unit_name")
    private String unitName;

    public ExportObject(ExportObjectWrapper converter) {
        this.bank = converter.getBank();
        this.coalition = converter.getCoalition();
        this.coalitionID = converter.getCoalitionID();
        this.country = converter.getCountry();
        this.groupName = converter.getGroupName();
        this.heading = converter.getHeading();
        this.name = converter.getName();
        this.pitch = converter.getPitch();
        this.runtimeID = converter.getRuntimeID();
        this.unitName = converter.getUnitName();

        this.flags = converter.getFlags();
        this.latLongAlt = converter.getLatLongAlt();
        this.position = converter.getPosition();
        this.type = converter.getType();
    }

    @Builder
    public ExportObject(Long id, double bank, String coalition, int coalitionID, int country, String groupName, double heading,
                        String name, double pitch, int runtimeID, String unitName,
                        Map<String, Boolean> flags, Map<String, Double> latLongAlt,
                        Map<String, Double> position, Map<String, Integer> type) {
        super(id);
        this.bank = bank;
        this.coalition = coalition;
        this.coalitionID = coalitionID;
        this.country = country;
        this.groupName = groupName;
        this.heading = heading;
        this.name = name;
        this.pitch = pitch;
        this.runtimeID = runtimeID;
        this.unitName = unitName;

        this.flags = flags;
        this.latLongAlt = latLongAlt;
        this.position = position;
        this.type = type;
    }

    @ElementCollection
    @MapKeyColumn(name="key")
    @Column(name="value")
    @CollectionTable(name="flags", joinColumns=@JoinColumn(name="id"))
    private Map<String, Boolean> flags;

    @ElementCollection
    @MapKeyColumn(name="key")
    @Column(name="value")
    @CollectionTable(name="lat_lon_alt", joinColumns=@JoinColumn(name="id"))
    private Map<String, Double> latLongAlt;

    @ElementCollection
    @MapKeyColumn(name="key")
    @Column(name="value")
    @CollectionTable(name="position", joinColumns=@JoinColumn(name="id"))
    private Map<String, Double> position;

    @ElementCollection
    @MapKeyColumn(name="key")
    @Column(name="value")
    @CollectionTable(name="type", joinColumns=@JoinColumn(name="id"))
    private Map<String, Integer> type;


    /**
     * Two ExportObject is consider equal if runtime id and unitName is the same.
     * @param o the object to be tested equality with.
     * @return boolean value indicating whether two ExportObject are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExportObject that = (ExportObject) o;

        if (runtimeID != that.runtimeID) return false;
        return Objects.equals(unitName, that.unitName);
    }

    @Override
    public int hashCode() {
        int result = (int) (runtimeID ^ (runtimeID >>> 32));
        result = 31 * result + (unitName != null ? unitName.hashCode() : 0);
        return result;
    }
}
