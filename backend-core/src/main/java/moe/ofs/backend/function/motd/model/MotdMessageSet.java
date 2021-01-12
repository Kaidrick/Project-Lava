package moe.ofs.backend.function.motd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.ofs.backend.domain.admin.PlayerRole;
import moe.ofs.backend.domain.dcs.BaseEntity;
import moe.ofs.backend.function.triggermessage.model.Message;

import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class MotdMessageSet extends BaseEntity {
    private Set<Message> messages;
    private String name;
    private long createTime;
    private long lastEditTime;
    private String language;  // TODO: use locale instead?
    private List<PlayerRole> roles;
}
