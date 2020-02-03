package plugin.greeting;

import core.LuaScripts;
import core.Plugin;
import core.box.BoxOfFlyableUnit;
import core.function.TriggerMessage;
import core.object.ExportObject;
import core.request.export.handler.ExportUnitSpawnObservable;
import core.request.server.ServerExecRequest;

import java.util.List;

public class Greeting implements Plugin {

    private static final List<String> greetingMessageList = null;

    public void register() {
        ExportUnitSpawnObservable exportUnitSpawnObservable = this::greet;
        exportUnitSpawnObservable.register();
    }

    private void greet(ExportObject unit) {
        if(unit.getFlags().get("Human")) {
            TriggerMessage.TriggerMessageBuilder builder = new TriggerMessage.TriggerMessageBuilder();
            builder.setMessage("Hello from 422d Backend Powered By Java 8")
                    .setReceiverGroupId(BoxOfFlyableUnit.getGroupId(unit.getGroupName()))
                    .setDuration(5).build().send();
        }
    }
}
