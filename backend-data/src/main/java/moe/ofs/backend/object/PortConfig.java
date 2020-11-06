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
@ApiModel(value = "PortConfig对象")
public class PortConfig {
    @ApiModelProperty(value = "服务器主端口", example = "3090")
    private int serverMainPort;

    @ApiModelProperty(value = "服务器轮训端口", example = "3080")
    private int serverPollPort;

    @ApiModelProperty(value = "Export端口", example = "3400")
    private int exportMainPort;

    @ApiModelProperty(value = "Export轮训端口", example = "3500")
    private int exportPollPort;
}
