package moe.ofs.backend.function.radiomenu;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class RadioItem {
    private int groupId;
    private String name;
    private RadioItem parent;
    private List<String> command;
    private String description;
    private Runnable action;
    private Runnable afterRemove;
    private Runnable beforeAdd;
    private Availability availability;

    public RadioItem(int groupId, String name) {
        // null parent, this is a root radio menu
        this.groupId = groupId;
        this.name = name;
        this.parent = null;
        this.availability = Availability.REUSE;

        List<String> list = new ArrayList<>();
        list.add(name);
        this.command = list;
    }

    public RadioItem(int groupId, String name, RadioItem parent) {
        this.groupId = groupId;
        this.name = name;
        this.parent = parent;
        this.availability = Availability.REUSE;
        List<String> list = parent != null ? new ArrayList<>(parent.getCommand()) : new ArrayList<>();
        list.add(name);
        this.command = list;
    }
}
