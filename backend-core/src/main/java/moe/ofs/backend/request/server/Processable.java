package moe.ofs.backend.request.server;

@FunctionalInterface
public interface Processable {
    void process(String object);
}
