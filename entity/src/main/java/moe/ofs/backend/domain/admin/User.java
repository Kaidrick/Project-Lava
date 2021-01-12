package moe.ofs.backend.domain.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.ofs.backend.domain.dcs.BaseEntity;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class User extends BaseEntity {
    private String ucid;
    private String userName;
    private String createTime;
    private String userLevelName;
    private int userLevel;
    private Set<PlayerRole> roles;
}
