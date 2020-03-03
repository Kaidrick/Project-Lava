package moe.ofs.backend.plugin.greeting;

import javafx.application.Platform;
import lombok.Getter;
import moe.ofs.backend.Plugin;
import moe.ofs.backend.gui.PluginListCell;
import moe.ofs.backend.handlers.BackgroundTaskRestartObservable;
import moe.ofs.backend.handlers.ExportUnitSpawnObservable;
import moe.ofs.backend.object.ExportObject;

import java.util.List;

/**
 * Greeting addon implements the functionality to send Message of the Day to players who just spawn into game
 * The message to be send should be read from a file? external or internal?
 * If there are multiple message to be sent, user should be able to specify the delay between each message
 */

public class Greeting implements Plugin {

    public final String name = "Server Greeting";
    public final String desc = "Say Hello on player spawn";

    private boolean isLoaded;

    private static final List<String> greetingMessageList = null;

    private ExportUnitSpawnObservable exportUnitSpawnObservable;
    private BackgroundTaskRestartObservable backgroundTaskRestartObservable;

    private PluginListCell pluginListCell;

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
//        if(unit.getFlags().get("Human")) {
//            TriggerMessage.TriggerMessageBuilder builder = new TriggerMessage.TriggerMessageBuilder();
//            builder.setMessage("Hello from 422d Backend Powered By Java 8")
//                    .setReceiverGroupId(BoxOfFlyableUnit.getGroupIdByName(unit.getGroupName()))
//                    .setDuration(5).build().send();
//        }
    }

    private void testMessageFunction(ExportObject object) {
        if(object.getFlags().get("Human")) {
            MessageQueue messageQueue = new MessageQueue(object);
            messageQueue.pend(new Message("Hello from 422d Backend Powered By Java 8", 3));
            messageQueue.pend(new Message("We (I mean, \"I\") are still working on " +
                    "some of the very fundamental" +
                    " features of the server.", 8));
            messageQueue.pend(new Message("Enjoy your stay here and fly safe!", 10));

            messageQueue.send();
        }
    }
}
