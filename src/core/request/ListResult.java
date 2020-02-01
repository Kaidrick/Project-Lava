package core.request;

import core.object.ExportObject;
import core.object.Group;
import core.object.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListResult<R> {
    private ArrayList<R> result;
    protected String tag;
    protected int total;

    public ArrayList<R> getResult() {
        return result;
    }

    public String getTag() {
        return tag;
    }

    public int getTotal() {
        return total;
    }
}

class MissionPollResult {
    private ArrayList<Unit> result;
    private String tag;
    private int total;

    public List<Unit> getResult() {
        return result;
    }

    public String getTag() {
        return tag;
    }

    public int getTotal() {
        return total;
    }
}


class ExportPollResult {
    private ArrayList<ExportObject> result;
    private String tag;
    private int total;

    public ArrayList<ExportObject> getResult() {
        return result;
    }

    public String getTag() {
        return tag;
    }

    public int getTotal() {
        return total;
    }
}
