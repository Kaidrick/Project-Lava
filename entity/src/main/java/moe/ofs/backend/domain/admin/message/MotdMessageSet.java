package moe.ofs.backend.domain.admin.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.ofs.backend.domain.admin.PlayerRoleGroup;
import moe.ofs.backend.domain.dcs.BaseEntity;
import moe.ofs.backend.domain.admin.message.Message;

import java.util.*;

/**
 * MotdMessageSet represents a set of message grouped by a name assigned for specific player role groups.
 * A MotdMessageSet can be assigned to be sent to multiple role groups; if a player has multiple role groups,
 * only the role group with the highest aggregated role level will be used to determine what messages to send
 * to this player.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MotdMessageSet extends BaseEntity {
    private final Set<Message> messages = new TreeSet<>(Comparator.comparingInt(Message::getIndex));
    private String name;
    private long createTime;
    private long lastEditTime;
    private String language;  // TODO: use locale instead?
    private Set<PlayerRoleGroup> assignedRoleGroups = new HashSet<>();
}
