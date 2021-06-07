package moe.ofs.backend.system.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class WebSocketAuthInfo {
    private String accessToken;
    private String oneTimeToken;
    private long timestamp;
}
