package moe.ofs.backend.object.tasks;

import lombok.Getter;

@Getter
public class WrappedAction extends Task {
    private WrappedActionParams params;

    {
        id = "WrappedAction";
    }

    public void setParams(WrappedActionParams params) {
        this.params = params;
    }
}
