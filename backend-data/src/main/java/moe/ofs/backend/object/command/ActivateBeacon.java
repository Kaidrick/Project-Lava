package moe.ofs.backend.object.command;

import lombok.Getter;

@Getter
public class ActivateBeacon extends Command {
    private ActivateBeaconParams params;

    {
        id = "ActivateBeacon";
    }

    public void setParams(ActivateBeaconParams params) {
        this.params = params;
    }
}
