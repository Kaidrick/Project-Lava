package moe.ofs.backend.security.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import moe.ofs.backend.dao.TokenInfoDao;
import moe.ofs.backend.domain.AdminInfo;
import moe.ofs.backend.domain.LavaUserToken;
import moe.ofs.backend.domain.TokenInfo;
import moe.ofs.backend.dto.AdminInfoDto;
import moe.ofs.backend.security.exception.token.RefreshTokenExpiredException;
import moe.ofs.backend.security.provider.PasswordTypeProvider;
import moe.ofs.backend.security.service.AccessTokenService;
import moe.ofs.backend.security.service.impl.AdminInfoMapService;
import moe.ofs.backend.security.token.PasswordTypeToken;
import moe.ofs.backend.vo.LavaUserTokenVo;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("token")
public class TokenController {
    private final AccessTokenService accessTokenService;
    private final TokenInfoDao tokenInfoDao;
    private final PasswordTypeProvider passwordTypeProvider;
    private final AdminInfoMapService adminInfoMapService;

    @PostMapping("/get")
    public LavaUserTokenVo getToken(
            String username,
            String password
    ) {
        PasswordTypeToken token = new PasswordTypeToken(username, password);
        Authentication authenticate = passwordTypeProvider.authenticate(token);
        AdminInfo adminInfo = (AdminInfo) authenticate.getPrincipal();

        tokenInfoDao.delete(Wrappers.<TokenInfo>lambdaQuery().eq(TokenInfo::getUserId, adminInfo.getId()));

        LavaUserToken generate = accessTokenService.generate();
        AdminInfoDto adminInfoDto = adminInfoMapService.adminInfoToDto(adminInfo);

        generate.setBaseUserInfoDto(adminInfoDto);
        TokenInfo tokenInfo = new TokenInfo(generate.getAccessToken(), adminInfo.getId(), generate.getAccessTokenExpireTime(), generate.getRefreshToken(), generate.getRefreshTokenExpireTime());
        tokenInfoDao.insert(tokenInfo);
        generate.setId(tokenInfo.getId());
        accessTokenService.add(generate);

        return lavaUserTokenToVo(generate);
    }

    @PostMapping("/refresh")
    public LavaUserTokenVo refreshToken(
            @RequestParam("refresh_token") String refreshToken
    ) {
        if (!accessTokenService.checkRefreshToken(refreshToken))
            throw new RefreshTokenExpiredException("RefreshToken已过期，请重新认证");
        LavaUserToken lavaUserToken = accessTokenService.refreshAccessToken(refreshToken);

        return lavaUserTokenToVo(lavaUserToken);
    }

    private LavaUserTokenVo lavaUserTokenToVo(LavaUserToken token) {
        return new LavaUserTokenVo(token.getAccessToken(), token.getRefreshToken(), token.getAccessTokenExpireTime());
    }

}
