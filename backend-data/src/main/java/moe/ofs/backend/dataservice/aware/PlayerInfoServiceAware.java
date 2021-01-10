package moe.ofs.backend.dataservice.aware;

import moe.ofs.backend.dataservice.player.PlayerInfoService;
import org.springframework.beans.factory.Aware;

public interface PlayerInfoServiceAware extends Aware {
    void setPlayerInfoService(PlayerInfoService playerInfoService);
}
