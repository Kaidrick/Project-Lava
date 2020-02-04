package plugin.greeting;

import core.box.BoxOfFlyableUnit;
import core.function.TriggerMessage;
import core.object.ExportObject;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class MessageQueue {
    private int receiverGroupId;
    private Queue<Message> messageQueue = new ArrayDeque<>();

    private MessageQueue() {}

    public MessageQueue(int receiverGroupId) {
        this.receiverGroupId = receiverGroupId;
    }

    public MessageQueue(ExportObject object) {
        this.receiverGroupId = BoxOfFlyableUnit.getGroupId(object.getGroupName());
    }

    public void pend(Message message) {
        messageQueue.offer(message);
    }

    public void send() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        messageQueue.forEach(m ->
                scheduledExecutorService.schedule(
                        () -> {
                            TriggerMessage.TriggerMessageBuilder builder = new TriggerMessage.TriggerMessageBuilder();
                            builder.setMessage(m.getContent())
                                    .setReceiverGroupId(receiverGroupId)
                                    .setDuration(m.getDuration()).build().send();
                        }, m.getWaitNextMessage(), TimeUnit.MILLISECONDS));

    }

}
