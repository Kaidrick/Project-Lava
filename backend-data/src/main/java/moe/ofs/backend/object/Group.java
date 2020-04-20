package moe.ofs.backend.object;

import java.util.*;


public class Group extends SimObject {
    private Integer modulation;
    private List<Object> tasks = new ArrayList<>();
    private Boolean radioSet;
    private String task = "CAS";
    private Boolean uncontrolled;
    private Route route;
    private Integer groupId;
    private Boolean hidden;
    private List<Unit> units = new ArrayList<>();
    private Double y;
    private Double x;
    private Boolean communication;
    private Double start_time;
    private Double frequency;

    public enum Category {
        AIRPLANE, HELICOPTER, GROUND, SHIP, TRAIN
    }

    public List<Unit> getUnits() {
        return units;
    }

    public static class GroupBuilder {
        private Group group = new Group();


        public GroupBuilder setName(String name) {
            group.name = name;
            return this;
        }
        public GroupBuilder setModulation(int modulation) {
            group.modulation = modulation;
            return this;
        }
        public GroupBuilder setUncontrolled(boolean uncontrolled) {
            group.uncontrolled = uncontrolled;
            return this;
        }
        public GroupBuilder setUnits(List<Unit> units) {
            group.units = new ArrayList<>(units);
            return this;
        }
        public GroupBuilder addUnit(Unit unit) {
            group.units.add(unit);
            return this;
        }
        public GroupBuilder setRoute(Route route) {
            group.route = route;
            return this;
        }
        public GroupBuilder setCategory(Category category) {
            group.category = category.ordinal();
            return this;
        }
        public GroupBuilder setCategory(int category) {
            group.category = category;
            return this;
        }

        public Group build() {
            return group;
        }
    }
}
