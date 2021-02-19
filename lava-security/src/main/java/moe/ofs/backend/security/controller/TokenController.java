package moe.ofs.backend.security.controller;

import lombok.RequiredArgsConstructor;
import moe.ofs.backend.dao.TokenInfoDao;
import moe.ofs.backend.domain.AdminInfo;
import moe.ofs.backend.domain.LavaUserToken;
import moe.ofs.backend.domain.TokenInfo;
import moe.ofs.backend.security.provider.PasswordTypeProvider;
import moe.ofs.backend.security.service.AccessTokenService;
import moe.ofs.backend.security.token.PasswordTypeToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @projectName: Project-Lava
 * @className: TokenController
 * @description:
 * @author: alexpetertyler
 * @date: 2021/2/9
 * @version: v1.0
 */
@RestController
@RequiredArgsConstructor
public class TokenController {
    private final AccessTokenService accessTokenService;
    private final TokenInfoDao tokenInfoDao;
    private final PasswordTypeProvider passwordTypeProvider;

    @PostMapping("/get/token")
    public LavaUserToken getToken(
            String username,
            String password
    ) {
        PasswordTypeToken token = new PasswordTypeToken(username, password);
        AdminInfo adminInfo = (AdminInfo) passwordTypeProvider.authenticate(token).getPrincipal();
        LavaUserToken generate = accessTokenService.generate();
        generate.setUserInfoToken(token);
        TokenInfo tokenInfo = new TokenInfo(generate.getAccessToken(), adminInfo.getId(), generate.getAccessTokenExpireTime(), generate.getRefreshToken(), generate.getRefreshTokenExpireTime());
        tokenInfoDao.insert(tokenInfo);
        generate.setId(tokenInfo.getId());
        accessTokenService.add(generate);

        generate.setId(null);
        generate.setUserInfoToken(null);
        return generate;
    }

    @PostMapping("/refresh/token")
    public LavaUserToken refreshToken(
            @RequestParam("refresh_token") String refreshToken
    ) {
        if (!accessTokenService.checkRefreshToken(refreshToken)) throw new RuntimeException("RefreshToken已过期，请重新认证");
        LavaUserToken lavaUserToken = accessTokenService.refreshAccessToken(refreshToken);
        lavaUserToken.setUserInfoToken(null);
        lavaUserToken.setId(null);
        return lavaUserToken;
    }
}
