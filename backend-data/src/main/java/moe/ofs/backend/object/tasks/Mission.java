package moe.ofs.backend.object.tasks;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mission extends Task {
    private MissionParams params;
}
