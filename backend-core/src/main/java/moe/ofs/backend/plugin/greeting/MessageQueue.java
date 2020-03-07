package moe.ofs.backend.plugin.greeting;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.function.TriggerMessage;
import moe.ofs.backend.services.FlyableUnitService;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class MessageQueue {
    private int receiverGroupId;
    private Queue<Message> messageQueue = new ArrayDeque<>();

    private FlyableUnitService flyableUnitService;

    private int nextTimeStamp;

    private MessageQueue() {}

    public MessageQueue(int receiverGroupId) {
        this.receiverGroupId = receiverGroupId;
    }

    public MessageQueue(ExportObject object, FlyableUnitService flyableUnitService) {

        this.flyableUnitService = flyableUnitService;

        Optional<Integer> id = flyableUnitService.findGroupIdByName(object.getGroupName());

        this.receiverGroupId = id.orElseThrow(() -> new RuntimeException("Group ID Not Found!"));
    }

    public void pend(Message message) {
        messageQueue.offer(message);
    }

    public int nextTime(Message message) {

        int currentTime = nextTimeStamp;
        nextTimeStamp += message.getWaitNextMessage();
        return currentTime;
    }

    public void send() {
        new Thread(() -> {
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            messageQueue.forEach(m ->
                    scheduledExecutorService.schedule(
                            () -> {
                                TriggerMessage.TriggerMessageBuilder builder = new TriggerMessage.TriggerMessageBuilder();
                                builder.setMessage(m.getContent())
                                        .setReceiverGroupId(receiverGroupId)
                                        .setDuration(m.getDuration())
                                        .setClearView(false).build().send();
                            }, nextTime(m), TimeUnit.SECONDS));
            scheduledExecutorService.shutdown();
            try {
                scheduledExecutorService.awaitTermination(nextTimeStamp + 30, TimeUnit.SECONDS);
                scheduledExecutorService.shutdownNow();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
