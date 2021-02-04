package moe.ofs.backend.system.controllers;

import cn.hutool.core.lang.tree.Tree;
import moe.ofs.backend.domain.admin.frontend.NavMenu;
import moe.ofs.backend.system.services.NavMenuManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("system/nav-menu")  // FIXME: fix underscore
public class NavMenuManageController {

    private final NavMenuManageService service;

    public NavMenuManageController(NavMenuManageService service) {
        this.service = service;
    }

    @GetMapping("list")
    public Set<NavMenu> getNavMenus() {
        return service.findAllNavMenu();
    }

    @GetMapping("tree")
    public List<Tree<Long>> getNavMenusTree() {
        return service.findAllNavMenuAsTree();
    }

    @PostMapping("add")
    public int addNavMenu(@RequestBody NavMenu menu) {
        return service.addNavMenu(menu);
    }

    @PostMapping("batch-update")
    public int updateNavMenu(@RequestBody List<NavMenu> menus) {
        service.updateNavMenus(menus);
        return 0;  // FIXME
    }

    @PostMapping("update")
    public int updateNavMenu(@RequestBody NavMenu menu) {
        return service.updateNavMenu(menu);
    }

    @PostMapping("delete")
    public int deleteNavMenu(@RequestBody NavMenu menu) {
        return service.deleteNavMenu(menu);
    }
}
