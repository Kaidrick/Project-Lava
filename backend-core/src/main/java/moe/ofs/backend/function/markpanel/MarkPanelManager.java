package moe.ofs.backend.function.markpanel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.interaction.TestButtonCommand;
import moe.ofs.backend.object.Vector3D;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Mark panel id start with 1 because lua index start with 1 instead of zero
 *
 *
 * Marker panels added by player through tactical map UI will have a index starting from PARK_PANEL_INDEX_0,
 * while the panels added from scripting engine will start from 1
 *
 * 38 * 5 F 190
 * or 22 chinese characters
 */

@Component
public class MarkPanelManager {

    private static long markPanelId;

    private static final long PARK_PANEL_INDEX_0 = 251658240;

    private static final int textMaxLength = 256;

    private Gson gson = new Gson();

    @PostConstruct
    private void initialize() {
        TestButtonCommand testButtonCommand = () -> getAllMarkPanels().forEach(p ->
                System.out.println(p.getIndex() + " / " + p.getContent()));
        testButtonCommand.attach();
    }

    /**
     * If there are other scripts loaded to the mission, it might break the index uniqueness
     * Therefore, the safe way to get a id is to acquire all panels and check for the largest index
     * @return nextId
     */
    private long getNextMarkPanelId() {
        List<MarkPanel> panels = getAllMarkPanels();

        Optional<MarkPanel> optional = panels.stream()
                .filter(p -> p.getIndex() < PARK_PANEL_INDEX_0)
                .max(Comparator.comparingLong(MarkPanel::getIndex));

        return optional.map(panel -> panel.getIndex() + 1).orElseGet(() -> ++markPanelId);
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
