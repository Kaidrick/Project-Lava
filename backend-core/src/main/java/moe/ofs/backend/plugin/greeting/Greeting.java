package moe.ofs.backend.plugin.greeting;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.Plugin;
import moe.ofs.backend.domain.admin.message.Message;
import moe.ofs.backend.function.triggermessage.model.MessageQueue;
import moe.ofs.backend.function.triggermessage.factories.MessageQueueFactory;
import moe.ofs.backend.handlers.BackgroundTaskRestartObservable;
import moe.ofs.backend.handlers.ExportUnitSpawnObservable;
import moe.ofs.backend.domain.dcs.poll.ExportObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Greeting addon implements the functionality to send Message of the Day to players who just spawn into game
 * The message to be send should be read from a file? external or internal?
 * If there are multiple message to be sent, user should be able to specify the delay between each message
 */
@Slf4j
@Component
public class Greeting implements Plugin {

    public final String name = "Server Greeting";
    public final String desc = "Say Hello on player spawn";

    private List<Message> list;

    public List<Message> getList() {
        return list;
    }

    public void setList(List<Message> list) {
        this.list = list;
    }

    private ExportUnitSpawnObservable exportUnitSpawnObservable;

    private BackgroundTaskRestartObservable backgroundTaskRestartObservable;

    private final MessageQueueFactory messageQueueFactory;

    @Autowired
    public Greeting(MessageQueueFactory messageQueueFactory) {
        this.messageQueueFactory = messageQueueFactory;

        // or load from xml file?
        list = new ArrayList<>();
    }

    @Override
    public void init() {
        log.info(getName() + " initialized");
    }

    @Override
    public void register() {
        exportUnitSpawnObservable = this::greet;
        exportUnitSpawnObservable.register();
    }

    @Override
    public void unregister() {
        exportUnitSpawnObservable.unregister();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return "0.0.1";
    }

    @Override
    public String getAuthor() {
        return "Project Lava";
    }

    @Override
    public String getDescription() {
        return desc;
    }

    private void greet(ExportObject unit) {
        testMessageFunction(unit);
    }

    private void testMessageFunction(ExportObject object) {
        if(object.getStatus().get("Human")) {
            messageQueueFactory.setExportObject(object);
            MessageQueue messageQueue = messageQueueFactory.getObject();
            if (messageQueue != null) {
                list.forEach(messageQueue::pend);

                messageQueue.send();
            } else {
                throw new RuntimeException("MessageQueueFactory failed to provide a new MessageQueue instance.");
            }

        }
    }
}

