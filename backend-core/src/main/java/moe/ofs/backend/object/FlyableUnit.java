package moe.ofs.backend.object;

import lombok.Data;

@Data
public class FlyableUnit {

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
}
