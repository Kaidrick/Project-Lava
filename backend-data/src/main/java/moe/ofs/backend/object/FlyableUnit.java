package moe.ofs.backend.object;

import lombok.Getter;
import lombok.Setter;
import moe.ofs.backend.domain.dcs.BaseEntity;

import java.util.Objects;

@Getter
@Setter

public class FlyableUnit extends BaseEntity {

    // TODO --> use a wrapper to comply with java naming convention? or use LinkedTreeMap<?, ?>

    private int group_id;
    private int unit_id;
    private String group_name;
    private String unit_name;
    private String type;
    private double x;
    private double y;
    private double heading;
    private String countryName;
    private int country_id;
    private String category;
    private String onboard_num;
    private String livery_id;
    private String start_type;
    private int airdromeId;
    private String parking_id;
    private String parking;

    @Override
    public String toString() {
        return String.format("Flyable %s %s (%s) starting at (%.0f,%.0f) HDG: %.0f in Group %d using livery \"%s\"",
                getCategory(), getType(), getUnit_id(),
                getX(), getY(), getHeadingForDisplay(),
                getGroup_id(), getLivery_id());
    }

    public double getHeadingForDisplay() {
        return Math.toDegrees(heading);
    }

    public int getParking() {
        return Integer.parseInt(parking);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlyableUnit that = (FlyableUnit) o;

        if (group_id != that.group_id) return false;
        if (unit_id != that.unit_id) return false;
        if (!Objects.equals(type, that.type)) return false;
        return Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        int result = group_id;
        result = 31 * result + unit_id;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }
}
