package moe.ofs.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import moe.ofs.backend.domain.admin.PlayerRoleGroup;

public interface PlayerRoleGroupDao extends BaseMapper<PlayerRoleGroup> {
    PlayerRoleGroup findRoleGroupWithRoles(Long id);

    PlayerRoleGroup findRolesGroupByName(String name);
}
