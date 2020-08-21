package moe.ofs.backend.object;

import lombok.Data;

@Data
public class PortConfig {
    private int serverMainPort;

    private int serverPollPort;

    private int exportMainPort;

    private int exportPollPort;
}
