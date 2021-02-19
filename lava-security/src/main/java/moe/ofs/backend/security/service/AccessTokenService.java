package moe.ofs.backend.security.service;

import moe.ofs.backend.domain.LavaUserToken;
import moe.ofs.backend.domain.TokenInfo;

import javax.annotation.PostConstruct;

public interface AccessTokenService {

    LavaUserToken generate();

    Long add(LavaUserToken lavaUserToken);

    void update(LavaUserToken lavaUserToken);

    void deleteById(Long id);

    void delete(LavaUserToken lavaUserToken);

    LavaUserToken getByAccessToken(String accessToken);

    LavaUserToken getByRefreshToken(String refreshToken);

    boolean checkAccessToken(String accessToken);

    boolean checkAccessToken(LavaUserToken lavaUserToken);

    boolean checkRefreshToken(String refreshToken);

    boolean checkRefreshToken(LavaUserToken lavaUserToken);

    LavaUserToken refreshAccessToken(String refreshToken);

    LavaUserToken refreshAccessToken(LavaUserToken lavaUserToken);

    @PostConstruct
    void collect();

    LavaUserToken tokenInfoToLavaUserToken(TokenInfo tokenInfo);

    void expire();
}
