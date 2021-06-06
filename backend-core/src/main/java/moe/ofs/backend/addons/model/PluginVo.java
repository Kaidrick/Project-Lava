package moe.ofs.backend.addons.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "PluginVo", description = "插件 Vo对象")
public class PluginVo {
    @ApiModelProperty(value = "id", example = "1")
    private Long id;

    @ApiModelProperty(value = "ident")
    private String ident;

    @ApiModelProperty(value = "名称", example = "Server Greeting")
    private String name;

    @ApiModelProperty(value = "描述", example = "Say Hello on player spawn")
    private String description;
}
