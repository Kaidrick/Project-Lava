package moe.ofs.backend.config.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@ApiModel(value = "ServerResetAction", description = "服务器重启指令")
public class ServerResetAction {
    @ApiModelProperty(value = "重启时间")
    private Instant restartTime;

    @ApiModelProperty(value = "重启原因", example = "正常计划重启")
    private String reason;

    @ApiModelProperty(value = "重启类型")
    private ResetType resetType;
}
