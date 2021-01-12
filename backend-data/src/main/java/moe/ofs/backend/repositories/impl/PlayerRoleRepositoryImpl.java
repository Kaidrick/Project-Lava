package moe.ofs.backend.repositories.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import moe.ofs.backend.dao.PlayerRoleDao;
import moe.ofs.backend.domain.admin.PlayerRole;
import moe.ofs.backend.repositories.PlayerRoleRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerRoleRepositoryImpl extends ServiceImpl<PlayerRoleDao, PlayerRole> implements PlayerRoleRepository {

}
