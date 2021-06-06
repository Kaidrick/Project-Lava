package moe.ofs.backend.domain.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import moe.ofs.backend.domain.dcs.BaseEntity;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@ApiModel(value = "PlayerRoleGroup", description = "用户角色组")
public class PlayerRoleGroup extends BaseEntity {
    @ApiModelProperty(value = "用户组名称", example = "user")
    private String roleGroupName;
    @ApiModelProperty(value = "角色列表")
    private List<PlayerRole> roles;
}
