package moe.ofs.backend.core.function;

import moe.ofs.backend.LuaScripts;
import moe.ofs.backend.core.request.server.ServerExecRequest;

public final class TriggerMessage {
    private String message = "Hello from 422d Backend Powered by Java 8";
    private int receiverGroupId = -1;
    private int duration = 5;
    private boolean clearView = false;
    private static String triggerMessageByGroupId = LuaScripts.load("send_message_by_group_id.lua");

    public static class TriggerMessageBuilder {
        private TriggerMessage triggerMessage = new TriggerMessage();
        public TriggerMessageBuilder setMessage(String message) {
            triggerMessage.message = message;
            return this;
        }

        public TriggerMessageBuilder setDuration(int duration) {
            triggerMessage.duration = duration;
            return this;
        }

        public TriggerMessageBuilder setReceiverGroupId(int groupId) {
            triggerMessage.receiverGroupId = groupId;
            return this;
        }

        public TriggerMessageBuilder setClearView(boolean clearView) {
            triggerMessage.clearView = clearView;
            return this;
        }

        public TriggerMessage build() {
            if(triggerMessage.receiverGroupId < 0) {
                throw new RuntimeException("GroupId is not specified for trigger message.");
            }
            return triggerMessage;
        }
    }

    private TriggerMessage() {}

    TriggerMessage(int receiverGroupId, String message) {
        this.message = message;
        this.receiverGroupId = receiverGroupId;
    }

    public void send() {
        String preparedString = String.format(triggerMessageByGroupId,
                receiverGroupId, message, duration, clearView);
        System.out.println(preparedString);
        new ServerExecRequest(preparedString).send();
    }
}
