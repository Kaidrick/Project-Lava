package moe.ofs.backend.function.spawncontrol.aspects;

public enum ControlAction {
    ADD(0), REMOVE(1), UPDATE(2), SPAWN(3), DESPAWN(4);

    private final int actionCode;

    ControlAction(int actionCode) {
        this.actionCode = actionCode;
    }

    public int getActionCode() {
        return actionCode;
    }

    public String getActionName() {
        return name().toLowerCase();
    }
}
