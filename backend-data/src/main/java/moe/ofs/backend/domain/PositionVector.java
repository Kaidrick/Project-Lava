package moe.ofs.backend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("VECTOR3D")
public class PositionVector implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    private double x;
    private double y;
    private double z;
}
