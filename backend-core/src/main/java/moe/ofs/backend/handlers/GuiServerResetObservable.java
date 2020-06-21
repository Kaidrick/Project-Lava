package moe.ofs.backend.handlers;

import java.util.ArrayList;
import java.util.List;

public interface GuiServerResetObservable {
    List<GuiServerResetObservable> list = new ArrayList<>();

    void observe();

    default void register() {
        list.add(this);
    }
    default void unregister() {
        list.remove(this);
    }

    static void invokeAll() {
        new ArrayList<>(list).forEach(GuiServerResetObservable::observe);
    }
}
