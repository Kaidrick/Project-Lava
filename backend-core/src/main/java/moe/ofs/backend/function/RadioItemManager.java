package moe.ofs.backend.function;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.handlers.BackgroundTaskRestartObservable;
import moe.ofs.backend.handlers.ControlPanelShutdownObservable;
import moe.ofs.backend.handlers.ExportUnitSpawnObservable;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.services.FlyableUnitService;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class RadioItemManager {

    static MissionStartObservable missionStartObservable;
    static BackgroundTaskRestartObservable backgroundTaskRestartObservable;
    static ControlPanelShutdownObservable controlPanelShutdownObservable;

    private final HashMap<Integer, List<RadioItem>> radioCommandGroupIdPathMap = new HashMap<>();
    private final Gson gson = new Gson();
    private ScheduledExecutorService radioPullExecutorService;
    private final Runnable getRadioPulls = () -> {
        new ServerDataRequest(LuaScripts.load("radio/pull_radio_commands.lua"))
                .addProcessable(s -> {
                    if(!s.equals("[]"))
                        System.out.println(s);

                    Type pushedCommandMapType = new TypeToken<Map<String, List<String>>>() {}.getType();
                    Map<String, List<String>> pushedCommandMap = gson.fromJson(s, pushedCommandMapType);

                    pushedCommandMap.keySet()
                            .forEach(groupId -> {
                                // TODO -> send to a blocking queue for worker
                                Optional<RadioItem> radioItemOptional =
                                        radioCommandGroupIdPathMap.get(Integer.parseInt(groupId)).stream()
                                                .filter(r -> r.getCommand().equals(pushedCommandMap.get(groupId)))
                                                .findAny();
                                radioItemOptional.ifPresent(this::relayCommand);
                            });
                }).send();
    };

    private final FlyableUnitService flyableUnitService;

    public RadioItemManager(FlyableUnitService flyableUnitService) {
        this.flyableUnitService = flyableUnitService;
    }

    @PostConstruct
    public void init() {
        System.out.println("Init RadioItemManager instance...");

        missionStartObservable = this::setUp;
        missionStartObservable.register();

        backgroundTaskRestartObservable = this::tearDown;
        backgroundTaskRestartObservable.register();

        controlPanelShutdownObservable = this::tearDown;
        controlPanelShutdownObservable.register();
    }

    // should only be set up once
    public void setUp() {
        // inject a lua table into mission runtime lua state
        // send a request to get all pulls every 100 milliseconds
        radioCommandGroupIdPathMap.clear();
        new ServerDataRequest(LuaScripts.load("radio/radio_commands_init.lua")).send();

        radioPullExecutorService = Executors.newSingleThreadScheduledExecutor();
        radioPullExecutorService.scheduleWithFixedDelay(getRadioPulls, 0, 100, TimeUnit.MILLISECONDS);

        ExportUnitSpawnObservable exportUnitSpawnObservable = unit -> {
            Optional<Integer> optional = flyableUnitService.findGroupIdByName(unit.getGroupName());
            optional.ifPresent(id -> {
                sanitizeGroupRadioControl(id);  // remove all previous radio controls if any

                radioCommandGroupIdPathMap.put(id, new ArrayList<>());

                RadioItem testMenu1 = newRadioControl(id, "test menu 1");
                RadioItem testMenu2 = newRadioControl(id, "test menu 2", null);
                RadioItem testCommand1 = newRadioControl(id, "test command1", testMenu1);
                testCommand1.setDescription("TRIGGER_WELCOME_MESSAGES");
                testCommand1.setAvailability(Availability.REMOVE_PARENT);
                testCommand1.setAction(() -> new TriggerMessage(id, "ok!").send());

                RadioItem testCommand2 = newRadioControl(id, "test command2", testMenu2);
                testCommand2.setDescription("I DON'T KNOW!");
                testCommand2.setAvailability(Availability.REMOVE);

                addRadioControlAsMenu(testMenu1);
                addRadioControlAsMenu(testMenu2);
                addRadioControlAsCommand(testCommand1);
                addRadioControlAsCommand(testCommand2);

            });
        };
        exportUnitSpawnObservable.register();
        System.out.println("radio set up");
    }

    public void tearDown() {
        if(radioPullExecutorService != null) {
            radioPullExecutorService.shutdown();
        }
        new ServerDataRequest("radio_commands = nil").send();
    }

    // match control by group id and path list?

    public void relayCommand(RadioItem control) {
        String action = control.getDescription();
        Availability availability = control.getAvailability();

        new Thread(control.getAction()).start();

        switch(availability) {
            case REMOVE:
                removeRadioControl(control);
                break;
            case REMOVE_PARENT:
                // what is the parent of this control?
                removeRadioControl(control.getParent());
                break;
            case REMOVE_PARENT_IF_EMPTY:
                // find in map and count how many radio control has the same parent?
                // if none match then remove
                List<RadioItem> list = radioCommandGroupIdPathMap.get(control.getGroupId());
                boolean emptyParent = list.stream().noneMatch(r -> r.getParent().equals(control.getParent()));
                if (emptyParent)
                    removeRadioControl(control.getParent());
                break;
        }
    }

    public void addRadioControlAsMenu(RadioItem control) {
        String preparedLuaString = LuaScripts.loadAndPrepare("radio/add_radio_menu_by_group_id.lua",
                control.getGroupId(), control.getName(), getParentTable(control));
        new ServerDataRequest(preparedLuaString).send();
        radioCommandGroupIdPathMap.get(control.getGroupId()).add(control);
    }

    public void addRadioControlAsCommand(RadioItem control) {
        String preparedLuaString = LuaScripts.loadAndPrepare("radio/add_radio_command_by_group_id.lua",
                control.getGroupId(), control.getName(), getParentTable(control));
        new ServerDataRequest(preparedLuaString).send();
        radioCommandGroupIdPathMap.get(control.getGroupId()).add(control);
    }

    public void removeRadioControl(RadioItem control) {
        String preparedString = LuaScripts.loadAndPrepare("radio/remove_radio_item_by_group_id.lua",
                control.getGroupId(), getPathTable(control));
        new ServerDataRequest(preparedString).send();
        radioCommandGroupIdPathMap.get(control.getGroupId()).remove(control);
    }

    public void sanitizeGroupRadioControl(int groupId) {
        String preparedString = LuaScripts.loadAndPrepare("radio/remove_radio_item_by_group_id.lua",
                groupId, "nil");
        new ServerDataRequest(preparedString).send();
    }

    public RadioItem newRadioControl(int groupId, String name) {
        return new RadioItem(groupId, name);
    }

    public RadioItem newRadioControl(int groupId, String name, RadioItem parentControl) {
        return new RadioItem(groupId, name, parentControl);
    }

    // if menu path list is empty, return a "nil" string to represent root menu
    public String getParentTable(RadioItem control) {
        RadioItem parentControl = control.getParent();
        if(parentControl == null) {
            return "nil";
        } else {
            List<String> parentCommandList = parentControl.getCommand();
            return "{" +
                    parentCommandList.stream()
                            .map(p -> "\"" + p + "\"")
                            .collect(Collectors.joining(", "))
                    + "}";
        }
    }

    // path table includes the parent table and the name of this menu
    public String getPathTable(RadioItem control) {
        List<String> list = new ArrayList<>(control.getCommand());
        return "{" +
                list.stream()
                        .map(p -> "\"" + p + "\"")
                        .collect(Collectors.joining(", "))
                + "}";
    }


}
