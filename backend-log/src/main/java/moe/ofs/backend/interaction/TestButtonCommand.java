package moe.ofs.backend.interaction;

import java.util.ArrayList;
import java.util.List;

public interface TestButtonCommand {
    List<TestButtonCommand> list = new ArrayList<>();

    void update();

    default void attach() {
        list.add(this);
    }

    default void dettach() {
        list.remove(this);
    }

    static void invokeAll() {
        list.forEach(TestButtonCommand::update);
    }
}
