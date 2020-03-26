package moe.ofs.backend.request;

@FunctionalInterface
public interface Processable {
    void process(String object);
}
