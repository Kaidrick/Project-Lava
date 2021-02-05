package moe.ofs.backend.http.security.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AdminInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理员名称
     */
    @TableId(value = "name", type = IdType.ASSIGN_ID)
    private String name;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 上次登录时间
     */
    private Date lastConnectTime;

    /**
     * 是否启用
     */
    @TableLogic(value = "true", delval = "false")
    private Boolean enable;

    public AdminInfo() {
        createTime = new Date();
    }
}
