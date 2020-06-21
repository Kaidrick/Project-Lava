package moe.ofs.backend.object;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class Payload implements Serializable {
    Map<String, Object> pylons;
    double fuel;
    int flare;
    int chaff;
    int gun;
}
