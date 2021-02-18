package moe.ofs.backend.function.triggermessage.model;

import moe.ofs.backend.domain.admin.message.Message;
import moe.ofs.backend.domain.admin.message.TriggerMessage;
import moe.ofs.backend.domain.dcs.poll.ExportObject;
import moe.ofs.backend.function.triggermessage.services.TriggerMessageService;
import moe.ofs.backend.dataservice.slotunit.FlyableUnitService;
import org.springframework.lang.NonNull;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageQueue {
    private final int receiverGroupId;
    private final Queue<Message> messageQueue = new ArrayDeque<>();

    private FlyableUnitService flyableUnitService;

    private TriggerMessageService triggerMessageService;

    private int nextTimeStamp;

    public MessageQueue(@NonNull ExportObject object, FlyableUnitService flyableUnitService,
                        TriggerMessageService triggerMessageService) {

        this.flyableUnitService = flyableUnitService;
        this.triggerMessageService = triggerMessageService;

        Optional<Integer> id = flyableUnitService.findGroupIdByName(object.getGroupName());

        this.receiverGroupId = id.orElseThrow(() -> new RuntimeException("Group ID Not Found!"));
    }

    public void pend(Message message) {
        messageQueue.offer(message);
    }

    private int nextTime(Message message) {

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
                                TriggerMessage triggerMessage = TriggerMessage.builder()
                                        .message(m.getContent())
                                        .receiverGroupId(receiverGroupId)
                                        .duration(m.getDuration())
                                        .clearView(false)
                                        .build();
                                triggerMessageService.sendTriggerMessage(triggerMessage);
                                System.out.println("triggerMessage = " + triggerMessage);
                            }, nextTime(m), TimeUnit.SECONDS));
            scheduledExecutorService.shutdown();
            try {
                boolean shutdown = scheduledExecutorService.awaitTermination(nextTimeStamp + 30, TimeUnit.SECONDS);
                if (!shutdown) {
                    scheduledExecutorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
