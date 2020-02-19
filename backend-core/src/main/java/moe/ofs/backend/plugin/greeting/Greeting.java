package moe.ofs.backend.plugin.greeting;

import moe.ofs.backend.Plugin;
import moe.ofs.backend.object.ExportObject;
import moe.ofs.backend.request.export.handler.ExportUnitSpawnObservable;

import java.util.List;


/**
 * Greeting addon implements the functionality to send Message of the Day to players who just spawn into game
 * The message to be send should be read from a file? external or internal?
 * If there are multiple message to be sent, user should be able to specify the delay between each message
 */

public class Greeting implements Plugin {

    private static final List<String> greetingMessageList = null;

    public void register() {
        ExportUnitSpawnObservable exportUnitSpawnObservable = this::greet;
        exportUnitSpawnObservable.register();
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

