package moe.ofs.backend.function.triggermessage;

public interface TriggerMessageService {
    TriggerMessage.TriggerMessageBuilder getTriggerMessageTemplate();

    void sendTriggerMessage(TriggerMessage triggerMessage);
}
