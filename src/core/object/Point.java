package core.object;

public class Point {
    private Double alt;
    private String action;
    private String alt_type;
    private Double speed;
    private Object task;
    private String type;
    private String name;
    private Integer airdromeId;
    private Double x;
    private Double y;

    public enum Action {
        FROM_PARKING_AREA("From Parking Area"), FROM_RUNWAY("From Runway");

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
        TakeOffParking, TakeOff;

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

        public PointBuilder setAlt(double alt) {
            point.alt = alt;
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

        public Point build() {
            return point;
        }
    }
}
