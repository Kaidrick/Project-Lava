package moe.ofs.backend.plugin.greeting;

import moe.ofs.backend.Plugin;
import moe.ofs.backend.PluginClassLoader;
import moe.ofs.backend.gui.PluginListCell;
import moe.ofs.backend.handlers.BackgroundTaskRestartObservable;
import moe.ofs.backend.handlers.ExportUnitSpawnObservable;
import moe.ofs.backend.domain.ExportObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Greeting addon implements the functionality to send Message of the Day to players who just spawn into game
 * The message to be send should be read from a file? external or internal?
 * If there are multiple message to be sent, user should be able to specify the delay between each message
 */

@Component
public class Greeting implements Plugin {

    public final String name = "Server Greeting";
    public final String desc = "Say Hello on player spawn";

    private boolean isLoaded;

    private ExportUnitSpawnObservable exportUnitSpawnObservable;
    private BackgroundTaskRestartObservable backgroundTaskRestartObservable;

    private PluginListCell pluginListCell;

    private final MessageQueueFactory messageQueueFactory;

    @Autowired
    public Greeting(MessageQueueFactory messageQueueFactory) {
        this.messageQueueFactory = messageQueueFactory;
    }

    @PostConstruct
    public void init() {
        System.out.println("Greeting plugin bean constructed...register");
        register();
        PluginClassLoader.loadedPluginSet.add(this);
    }

    @Override
    public PluginListCell getPluginListCell() {
        return pluginListCell;
    }

    @Override
    public void setPluginListCell(PluginListCell cell) {
        pluginListCell = cell;
    }

    @Override
    public void register() {
        exportUnitSpawnObservable = this::greet;
        exportUnitSpawnObservable.register();
        isLoaded = true;
    }

    @Override
    public void unregister() {
        exportUnitSpawnObservable.unregister();
        isLoaded = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    private void greet(ExportObject unit) {
        testMessageFunction(unit);
    }

    private void testMessageFunction(ExportObject object) {
        if(object.getFlags().get("Human")) {
            messageQueueFactory.setExportObject(object);
            MessageQueue messageQueue = messageQueueFactory.getObject();
            if (messageQueue != null) {
                messageQueue.pend(new Message("Hello from 422d Backend Powered By Java 8", 3));
                messageQueue.pend(new Message("We (I mean, \"I\") are still working on " +
                        "some of the very fundamental" +
                        " features of the server.", 8));
                messageQueue.pend(new Message("Enjoy your stay here and fly safe!", 10));

                messageQueue.send();
            } else {
                throw new RuntimeException("MessageQueueFactory failed to provide a new MessageQueue instance.");
            }

        }
    }
}

