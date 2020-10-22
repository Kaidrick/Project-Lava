package moe.ofs.backend.connector.services;

public interface DataCycleUpdateService<T> {
    void add(T t);

    void remove(T t);

    void update(T t);

    boolean compare(T t1, T t2);
}
