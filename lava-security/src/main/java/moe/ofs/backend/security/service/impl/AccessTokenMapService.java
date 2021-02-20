package moe.ofs.backend.security.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import moe.ofs.backend.common.AbstractMapService;
import moe.ofs.backend.dao.TokenInfoDao;
import moe.ofs.backend.domain.LavaUserToken;
import moe.ofs.backend.domain.TokenInfo;
import moe.ofs.backend.domain.dcs.BaseEntity;
import moe.ofs.backend.security.service.AccessTokenService;
import moe.ofs.backend.security.token.PasswordTypeToken;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccessTokenMapService extends AbstractMapService<LavaUserToken> implements AccessTokenService {
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
        return save(lavaUserToken).getId();
    }

    public void update(LavaUserToken lavaUserToken) {
        add(lavaUserToken);
        TokenInfo tokenInfo = tokenInfoDao.selectById(lavaUserToken.getId());
        if (tokenInfo == null) {
            super.delete(lavaUserToken);
            lavaUserToken.setUserInfoToken(null);
            throw new RuntimeException("token ：" + new Gson().toJson(lavaUserToken) + " 库表不存在！");
        }
        tokenInfo.setAccessTokenExpireTime(lavaUserToken.getAccessTokenExpireTime());
        tokenInfo.setAccessToken(lavaUserToken.getAccessToken());
        tokenInfoDao.updateById(tokenInfo);
    }

    @Override
    public void deleteById(Long id) {
        super.deleteById(id);
        tokenInfoDao.deleteById(id);
    }

    @Override
    public void delete(LavaUserToken lavaUserToken) {
        this.deleteById(lavaUserToken.getId());
    }

    @Override
    public LavaUserToken getByUserName(String userName) {
        List<LavaUserToken> collect = findAll().stream().filter(v -> {
            Authentication token = (Authentication) v.getUserInfoToken();
            return token.getName().equals(userName);
        }).collect(Collectors.toList());
        if (collect.isEmpty()) throw new RuntimeException("userName不存在，请检查");
        return collect.get(0);
    }

    public LavaUserToken getByAccessToken(String accessToken) {
        List<LavaUserToken> collect = findAll()
                .stream()
                .filter(v -> v.getAccessToken().equals(accessToken))
                .collect(Collectors.toList());
        if (collect.size() == 1) return collect.get(0);

        TokenInfo tokenInfo = tokenInfoDao.selectOneByAccessToken(accessToken);

        if (tokenInfo == null) throw new RuntimeException("AccessToken不存在，请重新获取");

        LavaUserToken lavaUserToken = tokenInfoToLavaUserToken(tokenInfo);
        add(lavaUserToken);
        return lavaUserToken;
    }

    public LavaUserToken getByRefreshToken(String refreshToken) {
        List<LavaUserToken> collect = findAll()
                .stream()
                .filter(v -> v.getRefreshToken().equals(refreshToken))
                .collect(Collectors.toList());
        if (collect.size() == 1) return collect.get(0);

        TokenInfo tokenInfo = tokenInfoDao.selectOneByRefreshToken(refreshToken);

        if (tokenInfo == null) throw new RuntimeException("RefreshToken不存在，请重新认证");

        LavaUserToken lavaUserToken = tokenInfoToLavaUserToken(tokenInfo);
        add(lavaUserToken);
        return lavaUserToken;
    }

    public boolean checkAccessToken(String accessToken) {
        return checkAccessToken(getByAccessToken(accessToken));
    }

    public boolean checkAccessToken(LavaUserToken lavaUserToken) {
        Date now = new Date();
        DateTime hour = DateUtil.offsetHour(now, 2);

        return DateUtil.isIn(lavaUserToken.getAccessTokenExpireTime(), now, hour);
    }

    public boolean checkRefreshToken(String refreshToken) {
        return checkRefreshToken(getByRefreshToken(refreshToken));
    }

    public boolean checkRefreshToken(LavaUserToken lavaUserToken) {
        Date now = new Date();
        DateTime week = DateUtil.offsetWeek(now, 2);

        return DateUtil.isIn(lavaUserToken.getAccessTokenExpireTime(), now, week);
    }

    public LavaUserToken refreshAccessToken(String refreshToken) {
        return refreshAccessToken(getByRefreshToken(refreshToken));
    }

    public LavaUserToken refreshAccessToken(LavaUserToken lavaUserToken) {
        LavaUserToken generate = generate();
        lavaUserToken.setAccessToken(generate.getAccessToken());
        lavaUserToken.setAccessTokenExpireTime(generate.getAccessTokenExpireTime());
        update(lavaUserToken);
        return lavaUserToken;
    }

    @PostConstruct
    public void collect() {
        List<TokenInfo> tokenInfos = tokenInfoDao.selectTokens();
        if (tokenInfos.isEmpty()) return;

        tokenInfos.forEach(v -> {
            LavaUserToken lavaUserToken = tokenInfoToLavaUserToken(v);
            add(lavaUserToken);
        });
    }

    public LavaUserToken tokenInfoToLavaUserToken(TokenInfo tokenInfo) {
        PasswordTypeToken token = new PasswordTypeToken(tokenInfo.getName(), null);
        LavaUserToken lavaUserToken = new LavaUserToken(token, tokenInfo.getAccessToken(), tokenInfo.getRefreshToken(), tokenInfo.getAccessTokenExpireTime(), tokenInfo.getRefreshTokenExpireTime());
        lavaUserToken.setId(tokenInfo.getId());
        return lavaUserToken;
    }

    @Scheduled(fixedDelay = 2 * 60 * 3600 * 1000L)
    public void expire() {
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
