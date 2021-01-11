package moe.ofs.backend.chatcmd.services;

import moe.ofs.backend.chatcmdnew.model.ChatCommandDefinition;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;

/**
 * If message is sent to user and is waiting for user input, check consequent messages
 *
 * how to intercept user chat message as input:
 *
 * Player id is not and is never reliable for identifying a player
 * use UCID only because context may contain some sensitive information such as validation code
 * and password
 *
 * 1. make the onPlayerTrySendChat checks a lua table that contains key value pairs of the a player input
 * such that:
 *
 */
public interface ChatMessageContextListenerService {

    void addContextForPlayer(ChatCommandDefinition definition, PlayerInfo playerInfo);

    void removeContextForPlayer(ChatCommandDefinition definition, PlayerInfo playerInfo);

    void removeAllContextForPlayer(PlayerInfo playerInfo);

    void startListener();

    void stopListener();
}
