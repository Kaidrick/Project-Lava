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
@TableName("EXPORT_OBJECTS")
@ApiModel(value="ExportObjects对象", description="")
public class ExportObjects implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("OWN_BANK")
    private Double ownBank;

    @TableField("COALITION")
    private String coalition;

    @TableField("COALITION_ID")
    private Integer coalitionId;

    @TableField("COUNTRY_ID")
    private Integer countryId;

    @TableField("GROUP_NAME")
    private String groupName;

    @TableField("OWN_HEADING")
    private Double ownHeading;

    @TableField("OWN_NAME")
    private String ownName;

    @TableField("OWN_PITCH")
    private Double ownPitch;

    @TableField("RUNTIME_ID")
    private Long runtimeId;

    @TableField("UNIT_NAME")
    private String unitName;

    @TableField("GEOPOSITION_ID")
    private Long geopositionId;

    @TableField("VECTOR3D_ID")
    private Long vector3dId;


}
