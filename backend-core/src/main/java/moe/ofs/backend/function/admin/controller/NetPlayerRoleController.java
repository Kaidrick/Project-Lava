package moe.ofs.backend.function.admin.controller;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import moe.ofs.backend.domain.admin.PlayerRole;
import moe.ofs.backend.domain.admin.PlayerRoleGroup;
import moe.ofs.backend.function.admin.services.NetPlayerRoleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("admin/role")
@Api(tags = "用户角色管理管理API")
@ApiSupport(author = "北欧式的简单")
public class NetPlayerRoleController {
    NetPlayerRoleService netPlayerRoleService;

    public NetPlayerRoleController(NetPlayerRoleService netPlayerRoleService) {
        this.netPlayerRoleService = netPlayerRoleService;
    }

    @PostMapping("list")
    @ApiOperation(value = "获取角色列表")
    public List<PlayerRoleGroup> listPlayerRoleGroups() {
        return netPlayerRoleService.findAllRoleGroup();
    }

    @PostMapping("test")
    @ApiOperation(value = "测试")
    public Set<PlayerRole> test(
            @ApiParam
            @RequestBody String ucid
    ) {
        return netPlayerRoleService.findPlayerRoles(ucid);
    }

    @PostMapping("add")
    @ApiOperation(value = "添加角色")
    public boolean addRole(
            @ApiParam
            @RequestBody PlayerRole playerRole
    ) {
        return netPlayerRoleService.addRole(playerRole);
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除角色")
    public boolean deleteRole(
            @ApiParam
            @RequestBody PlayerRole playerRole
    ) {
        if (playerRole.getId() != null) {
            return netPlayerRoleService.deleteRole(playerRole);
        } else {
            return false;
        }
    }
}
