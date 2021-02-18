package moe.ofs.backend.function.triggermessage.services;

import lombok.NonNull;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;
import moe.ofs.backend.domain.admin.message.TriggerMessage;

import java.util.List;

/**
 * NetMessageService provides standardized methods for sending chat/net message to a connected player or a list
 * of players in the server.
 */
@NonNull
public interface NetMessageService {
    /**
     * Send a {@link String} as net/chat message to all connected players.
     * @param message The message to be sent to player.
     */
    void sendNetMessageToAll(String message);

    /**
     * Send a {@link TriggerMessage} as net/chat message to all connected players.
     * @param message The trigger message to be sent to player.
     */
    void sendNetMessageToAll(TriggerMessage message);

    /**
     * Send a {@link TriggerMessage} as net/chat message to a connected player. Field other than the
     * {@link TriggerMessage#getMessage()} will always be ignored.
     * @param message Trigger message to be sent to the player.
     * @param player The player who will receive this message. If player is null, the message will be sent
     *               to all players as net/chat message.
     */
    void sendNetMessageForPlayer(TriggerMessage message, PlayerInfo player);

    /**
     * Send a {@link TriggerMessage} as net/chat message to a list of players. Field other than the
     * {@link TriggerMessage#getMessage()} will always be ignored.
     * @param message The trigger message to be sent to the list of players.
     * @param players The list of players who will receive this message. If players it null, it is effectively
     *                the same as {@link NotificationMessageService#}
     */
    void sendNetMessageForPlayers(TriggerMessage message, List<PlayerInfo> players);

    /**
     * Send a {@link String} as net/chat message content to a player.
     * @param messageContent The message to be sent to the player.
     * @param playerInfo The player who will receive the message.
     */
    void sendNetMessageForPlayer(String messageContent, PlayerInfo playerInfo);

    /**
     * Send a {@link String} as net/chat message content to a list of players.
     * @param messageContent The message to be sent to a list of players.
     * @param players The list of player who will receive the message.
     */
    void sendNetMessageForPlayers(String messageContent, List<PlayerInfo> players);

    /**
     * Send a {@link String} as net/chat message to a player which will be shown as a message from another player.
     * This message will be hidden from anyone other than player who sent the message and player who will
     * receive the message.
     * @param messageContent The message to be sent to the player.
     * @param from The player who sent the message.
     * @param to The player who will receive the message.
     */
    void whisperNetMessageToPlayer(String messageContent, PlayerInfo from, PlayerInfo to);

    /**
     * Send a {@link String} as net/chat message to a list of players which will be shown as a message sent by
     * another player. This message will be hidden from anyone other than the player who sent the message and
     * specified players who will receive the message. It can be very useful for group messages.
     * @param messageContent The message to be sent to the list of players.
     * @param from The message originator.
     * @param players The list of message receivers.
     */
    void whisperNetMessageToPlayers(String messageContent, PlayerInfo from, List<PlayerInfo> players);
}
