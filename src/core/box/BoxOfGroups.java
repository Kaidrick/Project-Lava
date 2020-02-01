package core.box;

import core.object.Group;

import java.util.concurrent.ConcurrentHashMap;

public class BoxOfGroups {
    public static ConcurrentHashMap<String, Group> box = new ConcurrentHashMap<>();

    // update box
    // if new unit, generate events
    // if unit no long exist, generate events
}
