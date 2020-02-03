package plugin.greeting;

import core.Plugin;
import core.box.BoxOfFlyableUnit;
import core.request.export.handler.ExportUnitSpawnObservable;
import core.request.server.ServerExecRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Greeting implements Plugin {

    // TODO --> need to make wrap lua code in a helper class

    private static String triggerMessageByGroupId;

    static {
        try {
            Path path = Paths.get("src/core/request/scripts/send_message_by_group_id.lua");
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            triggerMessageByGroupId = bufferedReader.lines()
                    .reduce((s1, s2) -> s1 + "\n" + s2)
                    .orElseThrow(() -> new RuntimeException("Error Reading Script: " + path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register() {
        ExportUnitSpawnObservable exportUnitSpawnObservable =
                unit -> {
                    Boolean playerControl = unit.getFlags().get("Human");
                    if(playerControl) {
//                        System.out.println(triggerMessageByGroupId);

                        String preparedString = String.format(triggerMessageByGroupId,
                                BoxOfFlyableUnit.getGroupId(unit.getGroupName()),
                                "Hello from 422d Backend Powered By Java", 5, "false");
                        System.out.println(preparedString);
                        new ServerExecRequest(preparedString).send();
                    }

                };
        exportUnitSpawnObservable.register();
    }
}
