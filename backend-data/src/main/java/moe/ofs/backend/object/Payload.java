package moe.ofs.backend.object;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Payload {
    Map<String, Object> pylons;
    double fuel;
    int flare;
    int chaff;
    int gun;
}
