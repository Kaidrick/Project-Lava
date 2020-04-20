package moe.ofs.backend.object;

import lombok.Getter;
import lombok.Setter;
import moe.ofs.backend.object.tasks.Task;

@Getter
@Setter
public class Point {
    private Double alt;
    private String action;
    private String alt_type;
    private Double speed;
    private Task task;
    private String type;
    private String name;
    private Integer airdromeId;
    private Double x;
    private Double y;

    public enum Action {
        FROM_PARKING_AREA("From Parking Area"), FROM_PARKING_AREA_HOT("From Parking Area Hot"),
        FROM_RUNWAY("From Runway"),
        FLY_OVER_POINT("Fly Over Point"), TURNING_POINT("Turning Point"), LANDING("Landing");

        private String action;
        Action(String action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return action;
        }
    }

    public enum PointType {
        TakeOffParking("TakeOffParking"), TakeOff("TakeOff"), TakeOffParkingHot("TakeOffParkingHot"),
        TurningPoint("Turning Point"), Land("Land");

        private String type;

        PointType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    public enum AltType {
        BARO, RADIO;

        @Override
        public String toString() {
            return this.name();
        }
    }

    public static Point ofParkingInitialPoint(int airdromeId) {
        return new PointBuilder().setAirdromeId(airdromeId)
                .setAction(Action.FROM_PARKING_AREA).setType(PointType.TakeOffParking).build();
    }

    public static Point ofTakeOffInitialPoint(int airdromeId) {
        return new PointBuilder().setAirdromeId(airdromeId)
                .setAction(Action.FROM_RUNWAY).setType(PointType.TakeOff).build();
    }

    public static class PointBuilder {
        private Point point = new Point();

        public PointBuilder setSpeed(double speed) {
            point.speed = speed;
            return this;
        }
        public PointBuilder setAlt(double alt) {
            point.alt = alt;
            return this;
        }
        public PointBuilder setAltType(AltType altType) {
            point.alt_type = altType.toString();
            return this;
        }
        public PointBuilder setAction(Action action) {
            point.action = action.toString();
            return this;
        }
        public PointBuilder setType(PointType pointType) {
            point.type = pointType.toString();
            return this;
        }
        public PointBuilder setPos(double x, double y) {
            setX(x); setY(y);
            return this;
        }
        public PointBuilder setX(double x) {
            point.x = x;
            return this;
        }
        public PointBuilder setY(double y) {
            point.y = y;
            return this;
        }
        public PointBuilder setAirdromeId(int airdromeId) {
            point.airdromeId = airdromeId;
            return this;
        }
        public PointBuilder setTask(Task task) {
            point.task = task;
            return this;
        }

        public Point build() {
            return point;
        }
    }
}
