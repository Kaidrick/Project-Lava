package moe.ofs.backend.function.triggermessage.services;

import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.function.triggermessage.model.MessageFallback;
import moe.ofs.backend.function.triggermessage.model.TriggerMessage;

import java.util.List;

/**
 * TriggerMessageService assures that a message will made its way to a player in either
 * {@link TriggerMessage} or net/chat message as fallback. {@link MessageFallback} can be used to specify
 * the level of fallback.
 */
public interface NotificationMessageService {
    /**
     * Attempt to send a {@link TriggerMessage} to the given player. If no matching group id for the player
     * could be found, the message will be sent as a chat/net message instead.
     * @param message the message to be sent to the player.
     * @param playerInfo The player who will receive the notification.
     */
    void notifyPlayer(TriggerMessage message, PlayerInfo playerInfo);

    /**
     * Attempt to send a {@link TriggerMessage} to the given player. If no matching group id for the player
     * could be found, and if {@link MessageFallback} is anything other than {@link MessageFallback#NO_FALLBACK},
     * it will make another attempt to send the message as a net/chat message.
     *
     * @param message the message text to be sent to the given player.
     * @param playerInfo the player who will receive the message.
     * @param fallback the fallback level to be used to handle this message transmission.
     */
    void notifyPlayer(TriggerMessage message, PlayerInfo playerInfo, MessageFallback fallback);

    /**
     * Attempt to send a {@link TriggerMessage} to the given list of players. If any of the player in the list
     * does not have a matching group id for the unit, the message will be sent a chat/net message instead.
     * @param message the message to be sent to players in the list.
     * @param players the players who will receive this notification.
     */
    void notifyPlayers(TriggerMessage message, List<PlayerInfo> players);

    /**
     * Attempt to send a {@link TriggerMessage} to the given list of players. If any of the player in the list
     * does not have a matching group id for the unit, and if {@link MessageFallback} is specified to anything
     * other than {@link MessageFallback#NO_FALLBACK}, another attempt will be made to send the message as a
     * net/chat message for players who cannot receive trigger messages.
     * @param message the message to be sent to players in the list.
     * @param players the players who will receive this notification.
     * @param fallback the fallback level to be used to handle this message transmission.
     */
    void notifyPlayers(TriggerMessage message, List<PlayerInfo> players, MessageFallback fallback);

}
