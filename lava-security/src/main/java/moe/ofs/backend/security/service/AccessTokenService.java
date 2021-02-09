package moe.ofs.backend.security.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import moe.ofs.backend.common.AbstractMapService;
import moe.ofs.backend.dao.TokenInfoDao;
import moe.ofs.backend.domain.LavaUserToken;
import moe.ofs.backend.domain.TokenInfo;
import moe.ofs.backend.domain.dcs.BaseEntity;
import moe.ofs.backend.security.token.PasswordTypeToken;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @projectName: Project-Lava
 * @className: AccessTokenService
 * @description:
 * @author: alexpetertyler
 * @date: 2021/2/9
 * @version: v1.0
 */
@Service
@RequiredArgsConstructor
public class AccessTokenService extends AbstractMapService<LavaUserToken> {
    private final TokenInfoDao tokenInfoDao;

    public LavaUserToken generate() {
        LavaUserToken token = new LavaUserToken();
        token.setAccessToken(RandomUtil.randomString(10));
        token.setRefreshToken(RandomUtil.randomString(20));
        Date now = new Date();
        token.setAccessTokenExpireTime(DateUtil.offsetHour(now, 2));
        token.setRefreshTokenExpireTime(DateUtil.offsetWeek(now, 2));
        return token;
    }

    public Long add(LavaUserToken lavaUserToken) {
        return add(lavaUserToken);
    }

    public LavaUserToken getByAccessToken(String accessToken) {
        List<LavaUserToken> collect = findAll()
                .stream()
                .filter(v -> v.getAccessToken().equals(accessToken))
                .collect(Collectors.toList());
        if (collect.size() == 1) return checkValidation(collect.get(0));

        TokenInfo tokenInfo = tokenInfoDao.selectOneByAccessToken(accessToken);

        if (tokenInfo == null) throw new RuntimeException("AccessToken不存在，请重新获取");

        PasswordTypeToken token = new PasswordTypeToken(tokenInfo.getName(), tokenInfo.getPassword());
        LavaUserToken lavaUserToken = new LavaUserToken(token, tokenInfo.getAccessToken(), tokenInfo.getRefreshToken(), tokenInfo.getAccessTokenExpireTime(), tokenInfo.getRefreshTokenExpireTime());
        lavaUserToken.setId(tokenInfo.getId());
        add(lavaUserToken);
        return checkValidation(lavaUserToken);
    }

    public LavaUserToken getByRefreshToken(String refreshToken) {
        List<LavaUserToken> collect = findAll()
                .stream()
                .filter(v -> v.getRefreshToken().equals(refreshToken))
                .collect(Collectors.toList());
        if (collect.size() == 1) return checkValidation(collect.get(0));

        TokenInfo tokenInfo = tokenInfoDao.selectOneByRefreshToken(refreshToken);

        if (tokenInfo == null) throw new RuntimeException("RefreshToken不存在，请重新认证");

        PasswordTypeToken token = new PasswordTypeToken(tokenInfo.getName(), tokenInfo.getPassword());
        LavaUserToken lavaUserToken = new LavaUserToken(token, tokenInfo.getAccessToken(), tokenInfo.getRefreshToken(), tokenInfo.getAccessTokenExpireTime(), tokenInfo.getRefreshTokenExpireTime());
        lavaUserToken.setId(tokenInfo.getId());
        add(lavaUserToken);
        return checkValidation(lavaUserToken);
    }

    private LavaUserToken checkValidation(LavaUserToken lavaUserToken) {
        Date now = new Date();
        DateTime hour = DateUtil.offsetHour(now, 2);
        DateTime week = DateUtil.offsetWeek(now, 2);
        if (!DateUtil.isIn(lavaUserToken.getAccessTokenExpireTime(), now, hour))
            throw new RuntimeException("AccessToken已过期！");

        if (!DateUtil.isIn(lavaUserToken.getRefreshTokenExpireTime(), now, week))
            throw new RuntimeException("RefreshToken已过期！");
        return lavaUserToken;
    }

    @PostConstruct
    public void collect() {
        List<TokenInfo> tokenInfos = tokenInfoDao.selectTokens();
        if (tokenInfos.isEmpty()) return;

        tokenInfos.forEach(v -> {
            PasswordTypeToken token = new PasswordTypeToken(v.getName(), v.getPassword());
            LavaUserToken lavaUserToken = new LavaUserToken(token, v.getAccessToken(), v.getRefreshToken(), v.getAccessTokenExpireTime(), v.getRefreshTokenExpireTime());
            lavaUserToken.setId(v.getId());
            add(lavaUserToken);
        });
    }

    @Scheduled(fixedDelay = 2 * 60 * 3600 * 1000L)
    public void dispose() {
        if (super.getNextId() == 1) return;
        Date date = new Date();
        DateTime week = DateUtil.offsetWeek(date, 2);
        List<LavaUserToken> collect = findAll()
                .stream()
                .filter(v -> !DateUtil.isIn(v.getRefreshTokenExpireTime(), date, week))
                .collect(Collectors.toList());
        if (collect.isEmpty()) return;

        List<Long> idset = collect.stream().map(BaseEntity::getId).collect(Collectors.toList());
        tokenInfoDao.deleteBatchIds(idset);
        idset.forEach(this::deleteById);
    }
}
