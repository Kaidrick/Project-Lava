package moe.ofs.backend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * AccessToken
     */
    private String accessToken;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * AccessToken过期时间
     */
    private Date accessTokenExpireTime;

    /**
     * RefreshToken
     */
    private String refreshToken;

    /**
     * RefreshToken过期时间
     */
    private Date refreshTokenExpireTime;

    @TableField(exist = false)
    private String name;

    @TableField(exist = false)
    private String password;

    public TokenInfo(String accessToken, Long userId, Date accessTokenExpireTime, String refreshToken, Date refreshTokenExpireTime) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshToken = refreshToken;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }
}
