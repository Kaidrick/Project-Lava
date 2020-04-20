package moe.ofs.backend.object.tasks.main;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Orbit extends MainTask {

    private OrbitParams params;

    {
        id = "Orbit";
    }
}
