package moe.ofs.backend.object.tasks.main;

public enum  OrbitPattern {
    CIRCULAR("Circle"), RACE_TRACK("Race-Track");

    private String type;

    OrbitPattern(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
