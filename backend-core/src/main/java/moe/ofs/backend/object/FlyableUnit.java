package moe.ofs.backend.object;

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

    public int getGroup_id() {
        return group_id;
    }

    public int getUnit_id() { return unit_id; }

    public String getGroup_name() {
        return group_name;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public String getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeading() {
        return heading;
    }

    public double getHeadingForDisplay() {
        return Math.toDegrees(heading);
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCategory() {
        return category;
    }

    public String getLivery_id() {
        return livery_id;
    }

    public String getStart_type() {
        return start_type;
    }

    public void setStart_type(String start_type) {
        this.start_type = start_type;
    }

    public int getCountry_id() {
        return country_id;
    }

    public String getOnboard_num() {
        return onboard_num;
    }

    public int getAirdromeId() {
        return airdromeId;
    }

    public String getParking_id() {
        return parking_id;
    }

    public int getParking() {
        return Integer.parseInt(parking);
    }
}
