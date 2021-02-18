package moe.ofs.backend.repositories;

import com.baomidou.mybatisplus.extension.service.IService;
import moe.ofs.backend.domain.admin.PlayerRoleGroup;

import java.util.List;

public interface PlayerRoleGroupRepository extends IService<PlayerRoleGroup> {
    PlayerRoleGroup findRoleGroupWithRoles(Long id);

    PlayerRoleGroup findRolesGroupByName(String name);

    List<PlayerRoleGroup> findAllRoleGroup();
}
