package moe.ofs.backend.object.tasks;

import lombok.Getter;
import lombok.Setter;
import moe.ofs.backend.object.Route;

@Getter
@Setter
public class MissionParams {
    boolean airborne;
    Route route;
}
