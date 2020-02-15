package ofs.backend.core.request.server;

@FunctionalInterface
public interface Processable {
    void process(String object);
}
