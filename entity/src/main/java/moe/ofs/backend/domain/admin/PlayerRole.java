package moe.ofs.backend.domain.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.ofs.backend.domain.dcs.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "PlayerRole", description = "用户角色")
public class PlayerRole extends BaseEntity {
    @ApiModelProperty(value = "角色名", example = "motd")
    private String roleName;
    @ApiModelProperty(value = "角色等级", example = "100")
    private int roleLevel;
}
