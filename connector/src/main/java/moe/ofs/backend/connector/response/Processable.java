package moe.ofs.backend.connector.response;

@FunctionalInterface
public interface Processable {
    void process(String object);
}
