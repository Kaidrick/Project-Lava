package moe.ofs.backend.services;

import org.springframework.beans.factory.Aware;

public interface PlayerInfoServiceAware extends Aware {
    void setPlayerInfoService(PlayerInfoService playerInfoService);
}
