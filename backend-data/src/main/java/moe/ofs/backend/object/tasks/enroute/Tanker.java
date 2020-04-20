package moe.ofs.backend.object.tasks.enroute;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tanker extends EnRouteTask {

    private TankerParams params;

    {
        id = "Tanker";
    }

    public Tanker() {

    }
}
