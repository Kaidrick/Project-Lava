package moe.ofs.backend.domain;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Arrays;

public class EventTypeSerializer implements JsonSerializer<EventType>, JsonDeserializer<EventType> {
    private EventType getTypeById(int id) {
        return Arrays.stream(EventType.values())
                .filter(eventType -> eventType.getId() == id)
                .findAny().orElse(EventType.EMPTY);
    }

    @Override
    public JsonElement serialize(EventType eventType, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(eventType.getId());
    }

    @Override
    public EventType deserialize(JsonElement jsonElement, Type type,
                                 JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            return getTypeById(jsonElement.getAsNumber().intValue());
        } catch (JsonParseException e) {
            return EventType.EMPTY;
        }
    }
}
