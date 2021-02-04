package moe.ofs.backend.system.services;

import cn.hutool.core.lang.tree.Tree;
import moe.ofs.backend.domain.admin.frontend.NavMenu;

import java.util.List;
import java.util.Set;

public interface NavMenuManageService {
    Set<NavMenu> findAllNavMenu();

    List<Tree<Long>> findAllNavMenuAsTree();

    int addNavMenu(NavMenu menu);

    int updateNavMenu(NavMenu menu);

    int updateNavMenus(List<NavMenu> menuList);

    int deleteNavMenu(NavMenu menu);

    int deleteNavMenuById(Long id);
}
