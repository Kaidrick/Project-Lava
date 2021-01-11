package moe.ofs.backend.discipline.service;

import moe.ofs.backend.domain.dcs.poll.PlayerInfo;

import java.time.Duration;

/**
 * PlayerBanKickService interface provides methods for banning or kicking player for inappropriate behaviors.
 */
public interface PlayerDisciplineService {

    /**
     * Permanently ban player with no specific reason.
     * @param player The PlayerInfo for the player to be banned.
     */
    void ban(PlayerInfo player);

    /**
     * Ban player for given duration.
     * @param player The PlayerInfo for the player to be banned.
     * @param duration The duration of ban period.
     */
    void ban(PlayerInfo player, Duration duration);

    /**
     * Permanent ban player with a specific reason.
     * @param player The PlayerInfo for the player to be banned.
     * @param reason String content that explains the cause of action.
     */
    void ban(PlayerInfo player, String reason);

    void ban(PlayerInfo player, String reason, Duration duration);

    void kick(PlayerInfo player);

    void kick(PlayerInfo player, String reason);

    void destroy(PlayerInfo playerInfo);
}
