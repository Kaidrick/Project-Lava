package moe.ofs.backend.domain.events;

import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(EventTypeSerializer.class)
public enum EventType {
    EMPTY(0);

    private final int id;

    EventType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
