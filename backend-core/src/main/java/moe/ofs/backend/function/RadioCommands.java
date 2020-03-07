package moe.ofs.backend.function;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;
import moe.ofs.backend.ControlPanelApplication;
import moe.ofs.backend.handlers.BackgroundTaskRestartObservable;
import moe.ofs.backend.handlers.ControlPanelShutdownObservable;
import moe.ofs.backend.handlers.ExportUnitSpawnObservable;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.services.FlyableUnitService;
import moe.ofs.backend.util.LuaScripts;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This class is a helper class for radio command
 */
public class RadioCommands {

    static MissionStartObservable missionStartObservable;
    static BackgroundTaskRestartObservable backgroundTaskRestartObservable;
    static ControlPanelShutdownObservable controlPanelShutdownObservable;

    private static final FlyableUnitService FLYABLE_UNIT_SERVICE =
            ControlPanelApplication.applicationContext.getBean(FlyableUnitService.class);

    // a radio control can be added as a menu or a item
    // a menu has zero or more items in it
    // a item is clickable and will call a function on click
    @Getter @Setter
    public static class RadioControl {
        private int groupId;
        private String name;
        private RadioControl parent;
        private List<String> command;
        private String description;
        private Runnable action;
        private Runnable afterRemove;
        private Runnable beforeAdd;
        private Availability availability;

        public enum Availability {
            REUSE,
            REMOVE,
            REMOVE_PARENT, REMOVE_PARENT_IF_EMPTY,
            REMOVE_ALL, REMOVE_ALL_IF_EMPTY
        }

        private RadioControl(int groupId, String name) {
            // null parent, this is a root radio menu
            this.groupId = groupId;
            this.name = name;
            this.parent = null;
            this.availability = Availability.REUSE;

            List<String> list = new ArrayList<>();
            list.add(name);
            this.command = list;
        }

        private RadioControl(int groupId, String name, RadioControl parent) {
            this.groupId = groupId;
            this.name = name;
            this.parent = parent;
            this.availability = Availability.REUSE;
            List<String> list = parent != null ? new ArrayList<>(parent.getCommand()) : new ArrayList<>();
            list.add(name);
            this.command = list;
        }
    }


    // map -> id of the group that send this pull / path of the radio command
    private static final HashMap<Integer, List<RadioControl>> radioCommandGroupIdPathMap = new HashMap<>();
    private static final Gson gson = new Gson();
    private static ScheduledExecutorService radioPullExecutorService;
    private static final Runnable getRadioPulls = () -> {
        new ServerDataRequest(LuaScripts.load("radio/pull_radio_commands.lua"))
                .addProcessable(s -> {
                    if(!s.equals("[]"))
                        System.out.println(s);

                    Type pushedCommandMapType = new TypeToken<Map<String, List<String>>>() {}.getType();
                    Map<String, List<String>> pushedCommandMap = gson.fromJson(s, pushedCommandMapType);

                    pushedCommandMap.keySet()
                            .forEach(groupId -> {
                                // TODO -> send to a blocking queue for worker
                                Optional<RadioControl> radioControlOptional =
                                        radioCommandGroupIdPathMap.get(Integer.parseInt(groupId)).stream()
                                                .filter(r -> r.command.equals(pushedCommandMap.get(groupId)))
                                                .findAny();
                                radioControlOptional.ifPresent(RadioCommands::relayCommand);
                            });
        }).send();
    };

    public static void init() {
        System.out.println("Init RadioCommands");

        missionStartObservable = RadioCommands::setUp;
        missionStartObservable.register();

        backgroundTaskRestartObservable = RadioCommands::tearDown;
        backgroundTaskRestartObservable.register();

        controlPanelShutdownObservable = RadioCommands::tearDown;
        controlPanelShutdownObservable.register();
    }

    // should only be set up once
    public static void setUp() {
        // inject a lua table into mission runtime lua state
        // send a request to get all pulls every 100 milliseconds
        radioCommandGroupIdPathMap.clear();
        new ServerDataRequest(LuaScripts.load("radio/radio_commands_init.lua")).send();

        radioPullExecutorService = Executors.newSingleThreadScheduledExecutor();
        radioPullExecutorService.scheduleWithFixedDelay(getRadioPulls, 0, 100, TimeUnit.MILLISECONDS);

        ExportUnitSpawnObservable exportUnitSpawnObservable = unit -> {
            Optional<Integer> optional = FLYABLE_UNIT_SERVICE.findGroupIdByName(unit.getGroupName());
            optional.ifPresent(id -> {
                sanitizeGroupRadioControl(id);  // remove all previous radio controls if any

                radioCommandGroupIdPathMap.put(id, new ArrayList<>());

                RadioControl testMenu1 = newRadioControl(id, "test menu 1");
                RadioControl testMenu2 = newRadioControl(id, "test menu 2", null);
                RadioControl testCommand1 = newRadioControl(id, "test command1", testMenu1);
                testCommand1.setDescription("TRIGGER_WELCOME_MESSAGES");
                testCommand1.setAvailability(RadioControl.Availability.REMOVE_PARENT);
                testCommand1.setAction(() -> new TriggerMessage(id, "ok!").send());

                RadioControl testCommand2 = newRadioControl(id, "test command2", testMenu2);
                testCommand2.setDescription("I DON'T KNOW!");
                testCommand2.setAvailability(RadioControl.Availability.REMOVE);

                addRadioControlAsMenu(testMenu1);
                addRadioControlAsMenu(testMenu2);
                addRadioControlAsCommand(testCommand1);
                addRadioControlAsCommand(testCommand2);

            });
        };
        exportUnitSpawnObservable.register();
        System.out.println("radio set up");
    }

    public static void tearDown() {
//        missionStartObservable.unregister();
//        backgroundTaskRestartObservable.unregister();
//        controlPanelShutdownObservable.unregister();

        radioPullExecutorService.shutdown();
        new ServerDataRequest("radio_commands = nil").send();
    }

    // match control by group id and path list?

    public static void relayCommand(RadioControl control) {
        String action = control.getDescription();
        RadioControl.Availability availability = control.getAvailability();

        new Thread(control.action).start();

        switch(availability) {
            case REMOVE:
                removeRadioControl(control);
                break;
            case REMOVE_PARENT:
                // what is the parent of this control?
                removeRadioControl(control.parent);
                break;
            case REMOVE_PARENT_IF_EMPTY:
                // find in map and count how many radio control has the same parent?
                // if none match then remove
                List<RadioControl> list = radioCommandGroupIdPathMap.get(control.groupId);
                boolean emptyParent = list.stream().noneMatch(r -> r.parent.equals(control.parent));
                if (emptyParent)
                    removeRadioControl(control.parent);
                break;
        }
    }

    public static void addRadioControlAsMenu(RadioControl control) {
        String preparedLuaString = LuaScripts.loadAndPrepare("radio/add_radio_menu_by_group_id.lua",
                control.getGroupId(), control.getName(), getParentTable(control));
        new ServerDataRequest(preparedLuaString).send();
        radioCommandGroupIdPathMap.get(control.getGroupId()).add(control);
    }

    public static void addRadioControlAsCommand(RadioControl control) {
        String preparedLuaString = LuaScripts.loadAndPrepare("radio/add_radio_command_by_group_id.lua",
                control.getGroupId(), control.getName(), getParentTable(control));
        new ServerDataRequest(preparedLuaString).send();
        radioCommandGroupIdPathMap.get(control.getGroupId()).add(control);
    }

    public static void removeRadioControl(RadioControl control) {
        String preparedString = LuaScripts.loadAndPrepare("radio/remove_radio_item_by_group_id.lua",
                control.getGroupId(), getPathTable(control));
        new ServerDataRequest(preparedString).send();
        radioCommandGroupIdPathMap.get(control.getGroupId()).remove(control);
    }

    public static void sanitizeGroupRadioControl(int groupId) {
        String preparedString = LuaScripts.loadAndPrepare("radio/remove_radio_item_by_group_id.lua",
                groupId, "nil");
        new ServerDataRequest(preparedString).send();
    }

    public static RadioControl newRadioControl(int groupId, String name) {
        return new RadioControl(groupId, name);
    }

    public static RadioControl newRadioControl(int groupId, String name, RadioControl parentControl) {
        return new RadioControl(groupId, name, parentControl);
    }

    // if menu path list is empty, return a "nil" string to represent root menu
    public static String getParentTable(RadioControl control) {
        RadioControl parentControl = control.getParent();
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
    public static String getPathTable(RadioControl control) {
        List<String> list = new ArrayList<>(control.getCommand());
        return "{" +
                list.stream()
                        .map(p -> "\"" + p + "\"")
                        .collect(Collectors.joining(", "))
                + "}";
    }
}
