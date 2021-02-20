package moe.ofs.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import moe.ofs.backend.domain.dcs.BaseEntity;

import java.util.Date;

/**
 * @projectName: Project-Lava
 * @className: AccessToken
 * @description:
 * @author: alexpetertyler
 * @date: 2021/2/9
 * @version: v1.0
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LavaUserToken extends BaseEntity {
    private Object userInfoToken;
    private String accessToken;
    private String refreshToken;
    private Date accessTokenExpireTime;
    private Date refreshTokenExpireTime;
}
