package moe.ofs.backend.admin.service;

import moe.ofs.backend.domain.PlayerInfo;

import java.time.Duration;

public interface PlayerBanKickService {
    void ban(PlayerInfo player);

    /**
     * Permanent ban player
     * @param player The PlayerInfo for the player to be banned
     * @param reason String content that explains the cause of action
     */
    void ban(PlayerInfo player, String reason);

    void ban(PlayerInfo player, String reason, Duration duration);

    void kick(PlayerInfo player);

    void kick(PlayerInfo player, String reason);
}
