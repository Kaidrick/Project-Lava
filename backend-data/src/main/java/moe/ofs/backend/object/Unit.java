package moe.ofs.backend.object;

import java.util.Map;

public class Unit extends SimObject {
    private Double alt;
    private String alt_type = "BARO";
    private String livery_id = "default_livery";
    private String skill = "High";
    private String parking;
    private Double speed;
    private String type;
    private Integer unitId;
    private Double psi;
    private String parking_id;
    private Double x;
    private Double y;
    private Double heading;
    private Payload payload;
    private Map<Object, Object> callsign;
    private String onboard_num;

    private String getParking() {
        return parking;
    }

    private void setParking(int parking) {
        this.parking = String.valueOf(parking);
    }

    public static class UnitBuilder {
        private Unit unit = new Unit();

        public UnitBuilder setName(String name) {
            unit.name = name;
            return this;
        }
        public UnitBuilder setLivery(String liveryName) {
            unit.livery_id = liveryName;
            return this;
        }
        public UnitBuilder setSkill(Skill skillLevel) {
            unit.skill = skillLevel.toString();
            return this;
        }
        public UnitBuilder setParking(int parking) {
            unit.setParking(parking);
            return this;
        }
        public UnitBuilder setSpeed(double speed) {
            unit.speed = speed;
            return this;
        }
        public UnitBuilder setType(String type) {
            unit.type = type;
            return this;
        }
        public UnitBuilder setPos(double x, double y) {
            setX(x); setY(y);
            return this;
        }
        public UnitBuilder setX(double x) {
            unit.x = x;
            return this;
        }
        public UnitBuilder setY(double y) {
            unit.y = y;
            return this;
        }
        public UnitBuilder setParkingId(int parkingId) {
            unit.parking_id = String.valueOf(parkingId);
            return this;
        }
        public UnitBuilder setHeading(double heading) {
            unit.heading = heading;
            return this;
        }
        public UnitBuilder setOnboardNum(String onboardNum) {
            unit.onboard_num = onboardNum;
            return this;
        }
        public UnitBuilder setCategory(Category category) {
            unit.category = category.ordinal();
            return this;
        }
        public UnitBuilder setPayload(Payload payload) {
            unit.payload = payload;
            return this;
        }
        public UnitBuilder setCallsign(Map<Object, Object> callsign) {
            unit.callsign = callsign;
            return this;
        }

        public Unit build() {
            return unit;
        }
    }

    public Group.GroupBuilder packToGroupBuilder() {
        Group.GroupBuilder groupBuilder = new Group.GroupBuilder();
        groupBuilder.addUnit(this);
        groupBuilder.setName(this.name + "_Group");
        return groupBuilder;
    }

    public Group.GroupBuilder toGroupBuilderWithRouteOfInitialParking(int airdromeId, int parking) {
        this.setParking(parking);
        Group.GroupBuilder groupBuilder = new Group.GroupBuilder();
        Point initPoint = Point.ofParkingInitialPoint(airdromeId);
        Route route = new Route();
        route.addPoint(initPoint);
        return groupBuilder.addUnit(this).setName("Group_" + this.name)
                .setRoute(route).setCategory(this.category);
    }

    public Group.GroupBuilder toGroupBuilderWithRouteOfIntialRunwayTakeOff(int airdromeId, int parking) {
        this.setParking(parking);  // runway parking of type 16 only
        Group.GroupBuilder groupBuilder = new Group.GroupBuilder();
        Point initPoint = Point.ofTakeOffInitialPoint(airdromeId);
        Route route = new Route();
        route.addPoint(initPoint);
        return groupBuilder.addUnit(this).setName("Group_" + this.name)
                .setRoute(route).setCategory(this.category);
    }

    public enum Skill {
        HIGH("High");

        private String skill;
        Skill(String name) {
            skill = name;
        }
        public String toString() { return skill; }
    }

    public enum Category {
        AIRPLANE, HELICOPTER, GROUND_UNIT, SHIP, STRUCTURE
    }

    public enum RefuelingSystem {
        BOOM_AND_RECEPTACLE,
        PROBE_AND_DROGUE
    }

    public enum SensorType {
        OPTIC, RADAR, IRST, RWR
    }

    public enum OpticType {
        TV, LLTV, IR
    }

    public enum RadarType {
        AS, SS
    }
}
