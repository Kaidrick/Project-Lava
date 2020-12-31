package moe.ofs.backend.function.triggermessage.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import moe.ofs.backend.util.LuaScripts;

@Data
@Builder
public final class TriggerMessage {
    @NonNull
    @Builder.Default
    private String message = "Greetings from Project Lava - Powered by Java and Spring Framework";

    private int receiverGroupId;

    @Builder.Default
    private int duration = 10;

    private boolean clearView;

    private TriggerMessage(String message, int receiverGroupId, int duration, boolean clearView) {
        if (message.isEmpty()) {
            throw new IllegalArgumentException("Trigger message content must not be empty.");
        }

        if (duration < 1) {
            throw new IllegalArgumentException("Trigger message display duration must be at least 1 second.");
        }

        this.message = message;
        this.receiverGroupId = receiverGroupId;
        this.duration = duration;
        this.clearView = clearView;
    }
}
