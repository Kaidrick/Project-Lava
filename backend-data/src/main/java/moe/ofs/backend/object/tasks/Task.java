package moe.ofs.backend.object.tasks;

import lombok.Getter;

@Getter
public abstract class Task {
    protected String id;

    protected Integer number;
    protected Boolean auto;
    protected Boolean enabled;

    public void setNumber(int number) {
        this.number = number;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
