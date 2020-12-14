package moe.ofs.backend.discipline.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.ofs.backend.hookinterceptor.HookProcessEntity;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerTryConnectRecord extends HookProcessEntity {
    private String ipaddr;
    private String playerName;
    private String ucid;
}
