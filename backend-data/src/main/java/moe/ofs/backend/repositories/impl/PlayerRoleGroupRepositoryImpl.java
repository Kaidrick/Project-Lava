package moe.ofs.backend.repositories.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import moe.ofs.backend.dao.PlayerRoleGroupDao;
import moe.ofs.backend.domain.admin.PlayerRoleGroup;
import moe.ofs.backend.repositories.PlayerRoleGroupRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerRoleGroupRepositoryImpl extends ServiceImpl<PlayerRoleGroupDao, PlayerRoleGroup>
        implements PlayerRoleGroupRepository {

    @Override
    public PlayerRoleGroup findRoleGroupWithRoles(Long id) {
        return getBaseMapper().findRoleGroupWithRoles(id);
    }

    @Override
    public PlayerRoleGroup findRolesGroupByName(String name) {
        return getBaseMapper().findRolesGroupByName(name);
    }
}
