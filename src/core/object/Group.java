package core.object;

import java.util.*;


public class Group extends SimObject {

    private int        size;
    private List<Unit> units;

    public int getSize() {
        return size;
    }

    public List<Unit> getUnits() {
        return units;
    }
}
