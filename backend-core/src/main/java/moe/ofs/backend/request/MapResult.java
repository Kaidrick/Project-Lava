package moe.ofs.backend.request;

import java.util.HashMap;

public class MapResult<R> {
    private HashMap<String, R> result;
    private String tag;
    private int total;

    public HashMap<String, R> getResult() {
        return result;
    }

    public String getTag() {
        return tag;
    }

    public int getTotal() {
        return total;
    }
}
