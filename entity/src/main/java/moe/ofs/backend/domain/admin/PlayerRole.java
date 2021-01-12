package moe.ofs.backend.domain.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.ofs.backend.domain.dcs.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerRole extends BaseEntity {
    private String roleName;
    private int roleLevel;
}
