package moe.ofs.backend.system.services.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import moe.ofs.backend.dao.NavMenuDao;
import moe.ofs.backend.domain.admin.frontend.NavMenu;
import moe.ofs.backend.repositories.NavMenuRepository;
import moe.ofs.backend.system.services.NavMenuManageService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class NavMenuManageServiceImpl implements NavMenuManageService {

    private final NavMenuDao navMenuDao;
    private final NavMenuRepository navMenuRepository;

    public NavMenuManageServiceImpl(NavMenuDao navMenuDao, NavMenuRepository navMenuRepository) {
        this.navMenuDao = navMenuDao;
        this.navMenuRepository = navMenuRepository;
    }

    @Override
    public Set<NavMenu> findAllNavMenu() {
        return new HashSet<>(navMenuDao.selectList(Wrappers.emptyWrapper()));
    }

    @Override
    public List<Tree<Long>> findAllNavMenuAsTree() {
        List<NavMenu> menus = navMenuDao.selectList(Wrappers.emptyWrapper());
        List<TreeNode<Long>> nodeList = CollUtil.newArrayList();

        menus.forEach(menu -> nodeList.add(new TreeNode<>(menu.getId(), menu.getPid(), menu.getName(), 0L)));
        return TreeUtil.build(nodeList, 0L);
    }

    @Override
    public int addNavMenu(NavMenu menu) {
        menu.setIdent(UUID.randomUUID().toString().replace("-", ""));
        return navMenuDao.insert(menu);
    }

    @Override
    public int updateNavMenu(NavMenu menu) {
        return navMenuDao.updateById(menu);
    }

    @Override
    public int updateNavMenus(List<NavMenu> menuList) {
        navMenuRepository.updateBatchById(menuList);
        return 0;
    }

    @Override
    public int deleteNavMenu(NavMenu menu) {
        return navMenuDao.deleteById(menu.getId());
    }

    @Override
    public int deleteNavMenuById(Long id) {
        return navMenuDao.deleteById(id);
    }
}
