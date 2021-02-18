package moe.ofs.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import moe.ofs.backend.domain.admin.PlayerRoleGroup;

import java.util.List;

public interface PlayerRoleGroupDao extends BaseMapper<PlayerRoleGroup> {
    PlayerRoleGroup findRoleGroupWithRoles(Long id);

    PlayerRoleGroup findRolesGroupByName(String name);

    List<PlayerRoleGroup> findAllRoleGroupWithRoles();
}
