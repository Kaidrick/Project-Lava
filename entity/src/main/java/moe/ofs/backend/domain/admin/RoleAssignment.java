package moe.ofs.backend.domain.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.ofs.backend.domain.dcs.BaseEntity;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoleAssignment extends BaseEntity {
    private String ucid;
    private Long roleId;
    private Date time;
}
