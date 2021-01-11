package moe.ofs.backend.function.triggermessage.services;

import moe.ofs.backend.domain.dcs.poll.PlayerInfo;
import moe.ofs.backend.function.triggermessage.model.TriggerMessage;

import java.util.List;

/**
 * TriggerMessageService provides standardized methods to send trigger message to a player or a list of players
 * who are currently active in mission.
 */
public interface TriggerMessageService {
    /**
     * Send a {@link TriggerMessage} to a player that is currently active in mission. If no message target group
     * id is specified (group id == 0), the message will be sent to all players.
     * @param triggerMessage the message to be sent to player.
     */
    void sendTriggerMessage(TriggerMessage triggerMessage);

    /**
     * Send a {@link TriggerMessage} to a player that is currently active in mission. Message target group id
     * will be ignored if player is not null.
     * @param message the trigger message to be sent to a player.
     * @param player the player who will receive this message.
     */
    void sendTriggerMessageForPlayer(TriggerMessage message, PlayerInfo player);

    /**
     * Send a {@link TriggerMessage} to a list of players who are currently active in mission. Message target
     * group id is always ignore.
     * @param message Trigger message to be sent to players.
     * @param players List for players who will receive the trigger message; if players is null, it is effectively
     *                the same as {@link TriggerMessageService#sendTriggerMessage(TriggerMessage)}; if players
     *                is an empty list the message will be sent to all players who are currently active in mission.
     */
    void sendTriggerMessageForPlayers(TriggerMessage message, List<PlayerInfo> players);
}
