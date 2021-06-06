package moe.ofs.backend.object;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "PortConfig", description = "端口配置")
public class PortConfig {
    @ApiModelProperty(value = "服务主端口", example = "3010")
    private int serverMainPort;

    @ApiModelProperty(value = "服务轮询端口", example = "3011")
    private int serverPollPort;

    @ApiModelProperty(value = "Export主端口", example = "3012")
    private int exportMainPort;

    @ApiModelProperty(value = "Export轮询端口", example = "3013")
    private int exportPollPort;
}
