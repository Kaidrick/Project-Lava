package moe.ofs.backend.domain.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import moe.ofs.backend.domain.dcs.BaseEntity;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class PlayerRoleGroup extends BaseEntity {
    private String roleGroupName;
    private List<PlayerRole> roles;
}
