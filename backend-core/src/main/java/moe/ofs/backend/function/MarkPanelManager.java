package moe.ofs.backend.function;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.interaction.TestButtonCommand;
import moe.ofs.backend.object.Vector3D;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Mark panel id start with 1 because lua index start with 1 instead of zero
 */

@Component
public class MarkPanelManager {

    private static long markPanelId;

    private Gson gson = new Gson();

    @PostConstruct
    private void initialize() {
        TestButtonCommand testButtonCommand = () -> getAllMarkPanels().forEach(System.out::println);
        testButtonCommand.attach();
    }

    private long getNextMarkPanelId() {
        return ++markPanelId;
    }

    public MarkPanel create(int groupId, String content, Vector3D position) {
        return MarkPanel.builder()
                .index(getNextMarkPanelId()).groupId(groupId).content(content).position(position)
                .build();
    }

    public MarkPanel create(int groupId, String content, Vector3D position, String message) {
        return MarkPanel.builder()
                .index(getNextMarkPanelId()).groupId(groupId).content(content).position(position)
                .messageOnCreation(message)
                .build();
    }

    public void addMarkPanel(MarkPanel panel) {
        String messageOnCreation = panel.getMessageOnCreation() != null ? panel.getMessageOnCreation() : "";

        String luaString = LuaScripts.loadAndPrepare("markpanel/add_mark_panel.lua",
                panel.getIndex(), panel.getContent(),
                panel.getPosition().getX(), panel.getPosition().getY(), panel.getPosition().getZ(),
                panel.getGroupId(), panel.isReadOnly(), messageOnCreation);

        new ServerDataRequest(luaString).send();
    }

    public void removeMarkPanel(MarkPanel panel) {
        new ServerDataRequest(LuaScripts.loadAndPrepare("markpanel/remove_mark_panel.lua", panel.getIndex()));
    }

    public List<MarkPanel> getAllMarkPanels() {
        String panelJsonString =
                ((ServerDataRequest) new ServerDataRequest(
                        LuaScripts.load("markpanel/get_all_marks.lua")).send()).get();
        Type panelListType = new TypeToken<List<MarkPanel>>() {}.getType();
        return gson.fromJson(panelJsonString, panelListType);
    }

}
