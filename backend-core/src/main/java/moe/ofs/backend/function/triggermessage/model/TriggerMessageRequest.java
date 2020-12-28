package moe.ofs.backend.function.triggermessage.model;

import lombok.Data;

import java.util.List;

@Data
public class TriggerMessageRequest {
    private String message;
    private MessageType type;
    private List<String> ucidList;
    private boolean clearView;
    private int duration;
    private boolean useTriggerMessageWhenPossible;
}
