package moe.ofs.backend.domain.request;

public class Result<T> {
    private T data;  // List, Map, String, Boolean or Number
    private int total;
    private boolean is_tail;

    public T getData() {
        return data;
    }
    public int getTotal() {
        return total;
    }
    public boolean isIs_tail() {
        return is_tail;
    }
}
