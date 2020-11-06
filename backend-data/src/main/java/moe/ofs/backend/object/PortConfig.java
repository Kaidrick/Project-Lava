package moe.ofs.backend.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PortConfig {
    private int serverMainPort;

    private int serverPollPort;

    private int exportMainPort;

    private int exportPollPort;
}
