package moe.ofs.backend.repositories.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import moe.ofs.backend.dao.NavMenuDao;
import moe.ofs.backend.domain.admin.frontend.NavMenu;
import moe.ofs.backend.repositories.NavMenuRepository;
import org.springframework.stereotype.Repository;

@Repository
public class NavMenuRepositoryImpl extends ServiceImpl<NavMenuDao, NavMenu> implements NavMenuRepository {

}
