package moe.ofs.backend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("LAVA_SYSTEM_LOG")
public class LavaSystemLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    private Integer id;

    @TableField("NAME")
    private String name;

    @TableField("MESSAGE")
    private String message;

    @TableField("SOURCE_NAME")
    private String sourceName;

    @TableField("LEVEL")
    private String level;


}
