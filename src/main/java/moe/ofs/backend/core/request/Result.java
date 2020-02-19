package moe.ofs.backend.core.request;

public class Result<T> {
    private T data;  // List, Map, String, Boolean or Number
    private int total;

    public T getData() {
        return data;
    }
    public int getTotal() {
        return total;
    }
}
