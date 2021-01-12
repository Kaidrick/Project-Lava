package moe.ofs.backend.function.admin.controller;

import moe.ofs.backend.domain.admin.PlayerRole;
import moe.ofs.backend.function.admin.services.NetPlayerRoleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("admin/role")
public class NetPlayerRoleController {
    NetPlayerRoleService netPlayerRoleService;

    public NetPlayerRoleController(NetPlayerRoleService netPlayerRoleService) {
        this.netPlayerRoleService = netPlayerRoleService;
    }

    @PostMapping("test")
    public Set<PlayerRole> test(@RequestBody String ucid) {
        return netPlayerRoleService.findPlayerRoles(ucid);
    }

    @PostMapping("add")
    public boolean addRole(@RequestBody PlayerRole playerRole) {
        return netPlayerRoleService.addRole(playerRole);
    }

    @PostMapping("delete")
    public boolean deleteRole(@RequestBody PlayerRole playerRole) {
        if (playerRole.getId() != null) {
            return netPlayerRoleService.deleteRole(playerRole);
        } else {
            return false;
        }
    }
}
