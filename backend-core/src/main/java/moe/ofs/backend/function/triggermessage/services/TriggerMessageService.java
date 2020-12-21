package moe.ofs.backend.function.triggermessage.services;

import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.function.triggermessage.model.TriggerMessage;

import java.util.List;

public interface TriggerMessageService {
    TriggerMessage.TriggerMessageBuilder getTriggerMessageTemplate();

    void sendTriggerMessage(TriggerMessage triggerMessage);

    void sendTriggerMessageForPlayer(TriggerMessage message, PlayerInfo player);

    void sendTriggerMessageForPlayers(TriggerMessage message, List<PlayerInfo> players);
}
