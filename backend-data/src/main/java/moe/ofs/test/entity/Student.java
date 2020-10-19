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
@TableName("STUDENT")
@ApiModel(value="Student对象", description="")
public class Student implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    private Integer id;

    @TableField("NAME")
    private String name;

    @TableField("PASSPORT_NUMBER")
    private String passportNumber;


}
