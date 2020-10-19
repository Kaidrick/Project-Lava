package moe.ofs.test.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Tyler
 * @since 2020-10-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("PLAYER_INFO")
@ApiModel(value="PlayerInfo对象", description="")
public class PlayerInfo implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("IPADDR")
    private String ipaddr;

    @TableField("LANG")
    private String lang;

    @TableField("NAME")
    private String name;

    @TableField("NET_ID")
    private Integer netId;

    @TableField("PILOT_ID")
    private Long pilotId;

    @TableField("PING")
    private Integer ping;

    @TableField("SIDE")
    private Integer side;

    @TableField("SLOT")
    private String slot;

    @TableField("STARTED")
    private Boolean started;

    @TableField("UCID")
    private String ucid;


}
